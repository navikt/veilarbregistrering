package no.nav.fo.veilarbregistrering.db.registrering

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.fo.veilarbregistrering.besvarelse.*
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.TekstForSporsmal
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.IOException
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDateTime

class SykmeldtRegistreringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : SykmeldtRegistreringRepository {

    override fun lagreSykmeldtBruker(bruker: SykmeldtRegistrering, aktorId: AktorId): Long {
        val id = nesteFraSekvens(SYKMELDT_REGISTRERING_SEQ)
        val besvarelse = bruker.besvarelse
        val teksterForBesvarelse = tilJson(bruker.teksterForBesvarelse)
        val params = mapOf(
                "id" to id,
                "aktor_id" to aktorId.aktorId,
                "opprettet" to Timestamp.valueOf(LocalDateTime.now()),
                "tekster" to teksterForBesvarelse,
                "fremtidig_situasjon" to besvarelse.fremtidigSituasjon?.toString(),
                "tilbake_etter_52_uker" to besvarelse.tilbakeIArbeid?.toString(),
                "nus_kode" to UtdanningUtils.mapTilNuskode(besvarelse.utdanning),
                "utdanning_bestatt" to besvarelse.utdanningBestatt?.toString(),
                "utdanning_godkjent" to besvarelse.utdanningGodkjent?.toString(),
                "andre_utfordringer" to besvarelse.andreForhold?.toString()
        )

        val sql = "INSERT INTO ${SYKMELDT_REGISTRERING}" +
                " (${allSykmeldtColumns.joinToString(", ")})" +
                " VALUES (:id, :aktor_id, :opprettet, :tekster, :fremtidig_situasjon, :tilbake_etter_52_uker," +
                " :nus_kode, :utdanning_bestatt, :utdanning_godkjent, :andre_utfordringer)"

        db.update(sql, params)
        return id
    }

    override fun hentSykmeldtregistreringForAktorId(aktorId: AktorId): SykmeldtRegistrering? {
        val sql = "SELECT * FROM ${SYKMELDT_REGISTRERING}" +
                " WHERE ${BrukerRegistreringRepositoryImpl.AKTOR_ID} = :aktor_id " +
                " ORDER BY ${SYKMELDT_REGISTRERING_ID} DESC" +
                " FETCH NEXT 1 ROWS ONLY"

        return db.query(sql, mapOf("aktor_id" to aktorId.aktorId), rowMapperSykmeldt).firstOrNull()
    }

    override fun finnSykmeldtRegistreringerFor(aktorId: AktorId): List<SykmeldtRegistrering> {
        val sql = "SELECT * FROM ${SYKMELDT_REGISTRERING}" +
                " WHERE ${BrukerRegistreringRepositoryImpl.AKTOR_ID} = :aktor_id"

        return db.query(sql, mapOf("aktor_id" to aktorId.aktorId), rowMapperSykmeldt)
    }

    private fun nesteFraSekvens(sekvensNavn: String): Long {
        return db.queryForObject("SELECT $sekvensNavn.nextval FROM DUAL", noParams, Long::class.java)!!
    }

    companion object {
        private val mapper = jacksonObjectMapper()

        private const val SYKMELDT_REGISTRERING = "SYKMELDT_REGISTRERING"
        private const val SYKMELDT_REGISTRERING_SEQ = "SYKMELDT_REGISTRERING_SEQ"
        private const val SYKMELDT_REGISTRERING_ID = "SYKMELDT_REGISTRERING_ID"

        val allSykmeldtColumns = listOf(
                SYKMELDT_REGISTRERING_ID,
                BrukerRegistreringRepositoryImpl.AKTOR_ID,
                BrukerRegistreringRepositoryImpl.OPPRETTET_DATO,
                BrukerRegistreringRepositoryImpl.TEKSTER_FOR_BESVARELSE,
                BrukerRegistreringRepositoryImpl.FREMTIDIG_SITUASJON,
                BrukerRegistreringRepositoryImpl.TILBAKE_ETTER_52_UKER,
                BrukerRegistreringRepositoryImpl.NUS_KODE,
                BrukerRegistreringRepositoryImpl.UTDANNING_BESTATT,
                BrukerRegistreringRepositoryImpl.UTDANNING_GODKJENT_NORGE,
                BrukerRegistreringRepositoryImpl.ANDRE_UTFORDRINGER
        )

        val rowMapperSykmeldt = RowMapper { rs, _ ->
            try {
                SykmeldtRegistrering()
                    .setId(rs.getLong(SYKMELDT_REGISTRERING_ID))
                    .setOpprettetDato(
                        rs.getTimestamp(BrukerRegistreringRepositoryImpl.OPPRETTET_DATO).toLocalDateTime()
                    )
                    .setTeksterForBesvarelse(readListOf(rs.getString(BrukerRegistreringRepositoryImpl.TEKSTER_FOR_BESVARELSE)))
                    .setBesvarelse(
                        Besvarelse(
                            fremtidigSituasjon = rs.getString(BrukerRegistreringRepositoryImpl.FREMTIDIG_SITUASJON)
                                ?.let(FremtidigSituasjonSvar::valueOf),
                            tilbakeIArbeid = rs.getString(BrukerRegistreringRepositoryImpl.TILBAKE_ETTER_52_UKER)
                                ?.let(TilbakeIArbeidSvar::valueOf),
                            utdanning = UtdanningUtils.mapTilUtdanning(rs.getString(BrukerRegistreringRepositoryImpl.NUS_KODE)),
                            utdanningBestatt = rs.getString(BrukerRegistreringRepositoryImpl.UTDANNING_BESTATT)
                                ?.let(UtdanningBestattSvar::valueOf),
                            utdanningGodkjent = rs.getString(BrukerRegistreringRepositoryImpl.UTDANNING_GODKJENT_NORGE)
                                ?.let(UtdanningGodkjentSvar::valueOf),
                            andreForhold = rs.getString(BrukerRegistreringRepositoryImpl.ANDRE_UTFORDRINGER)
                                ?.let(AndreForholdSvar::valueOf),
                        )
                    )
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }

        fun tilJson(obj: List<TekstForSporsmal>): String =
                try {
                    ObjectMapper().writeValueAsString(obj)
                } catch (e: JsonProcessingException) {
                    "[]"
                }

        private inline fun <reified T> readListOf(json: String?) : List<T> =
                try {
                    json?.let {
                        val type = mapper.typeFactory.constructParametricType(List::class.java, T::class.java)
                        jacksonObjectMapper().readValue<List<T>>(it, type)
                    } ?: emptyList()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

        private val noParams = emptyMap<String, Any>()
    }
}