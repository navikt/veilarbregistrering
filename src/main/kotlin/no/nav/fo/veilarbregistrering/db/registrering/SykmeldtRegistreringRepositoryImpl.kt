package no.nav.fo.veilarbregistrering.db.registrering

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.fo.veilarbregistrering.besvarelse.*
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistrering
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.TekstForSporsmal
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.IOException
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDateTime

class SykmeldtRegistreringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : SykmeldtRegistreringRepository {

    override fun lagreSykmeldtBruker(sykmeldtRegistrering: SykmeldtRegistrering, bruker: Bruker): Long {
        val id = nesteFraSekvens(SYKMELDT_REGISTRERING_SEQ)
        val besvarelse = sykmeldtRegistrering.besvarelse
        val teksterForBesvarelse = tilJson(sykmeldtRegistrering.teksterForBesvarelse)
        val params = mapOf(
                "id" to id,
                "aktor_id" to bruker.aktorId.aktorId,
                "foedselsnummer" to bruker.gjeldendeFoedselsnummer.foedselsnummer,
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
                " VALUES (:id, :aktor_id, :foedselsnummer, :opprettet, :tekster, :fremtidig_situasjon, :tilbake_etter_52_uker," +
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
        val sql = if (isOnPrem()) "SELECT $sekvensNavn.nextval FROM DUAL" else "SELECT nextVal('$sekvensNavn')"
        return db.queryForObject(sql, noParams, Long::class.java)!!
    }

    override fun finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer(maksAntall: Int): List<AktorId> {
        val sql = "SELECT DISTINCT $AKTOR_ID FROM $SYKMELDT_REGISTRERING " +
                "WHERE $FOEDSELSNUMMER IS NULL LIMIT $maksAntall"
        return db.query(sql) { rs, _ -> AktorId(rs.getString("$AKTOR_ID")) }
    }

    override fun oppdaterSykmeldtRegistreringerMedManglendeFoedselsnummer(aktorIdFoedselsnummerMap: Map<AktorId, Foedselsnummer>): IntArray {
        val sql = "UPDATE $SYKMELDT_REGISTRERING SET $FOEDSELSNUMMER = :foedselsnummer WHERE $AKTOR_ID = :aktorId AND $FOEDSELSNUMMER IS NULL"

        val params = aktorIdFoedselsnummerMap.map { aktorIdFoedselsnummer ->
            mapOf(
                "aktorId" to aktorIdFoedselsnummer.key.aktorId,
                "foedselsnummer" to aktorIdFoedselsnummer.value.foedselsnummer)
        }

        return db.batchUpdate(sql, params.toTypedArray())
    }

    companion object {
        private val mapper = jacksonObjectMapper()

        private const val SYKMELDT_REGISTRERING = "SYKMELDT_REGISTRERING"
        private const val SYKMELDT_REGISTRERING_SEQ = "SYKMELDT_REGISTRERING_SEQ"
        private const val SYKMELDT_REGISTRERING_ID = "SYKMELDT_REGISTRERING_ID"
        private const val FREMTIDIG_SITUASJON = "FREMTIDIG_SITUASJON"
        private const val TILBAKE_ETTER_52_UKER = "TILBAKE_ETTER_52_UKER"
        private const val OPPRETTET_DATO = "OPPRETTET_DATO"
        private const val NUS_KODE = "NUS_KODE"
        private const val TEKSTER_FOR_BESVARELSE = "TEKSTER_FOR_BESVARELSE"
        private const val ANDRE_UTFORDRINGER = "ANDRE_UTFORDRINGER"
        private const val UTDANNING_BESTATT = "UTDANNING_BESTATT"
        private const val UTDANNING_GODKJENT_NORGE = "UTDANNING_GODKJENT_NORGE"
        private const val AKTOR_ID = "AKTOR_ID"
        private const val FOEDSELSNUMMER = "FOEDSELSNUMMER"

        val allSykmeldtColumns = listOf(
                SYKMELDT_REGISTRERING_ID,
                AKTOR_ID,
                FOEDSELSNUMMER,
                OPPRETTET_DATO,
                TEKSTER_FOR_BESVARELSE,
                FREMTIDIG_SITUASJON,
                TILBAKE_ETTER_52_UKER,
                NUS_KODE,
                UTDANNING_BESTATT,
                UTDANNING_GODKJENT_NORGE,
                ANDRE_UTFORDRINGER
        )

        val rowMapperSykmeldt = RowMapper { rs, _ ->
            try {
                SykmeldtRegistrering(
                    id=(rs.getLong(SYKMELDT_REGISTRERING_ID)),
                    opprettetDato=(
                        rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime()
                    ),
                    teksterForBesvarelse=(readListOf(rs.getString(TEKSTER_FOR_BESVARELSE))),
                    besvarelse=(
                        Besvarelse(
                            fremtidigSituasjon = rs.getString(FREMTIDIG_SITUASJON)
                                ?.let(FremtidigSituasjonSvar::valueOf),
                            tilbakeIArbeid = rs.getString(TILBAKE_ETTER_52_UKER)
                                ?.let(TilbakeIArbeidSvar::valueOf),
                            utdanning = UtdanningUtils.mapTilUtdanning(rs.getString(NUS_KODE)),
                            utdanningBestatt = rs.getString(UTDANNING_BESTATT)
                                ?.let(UtdanningBestattSvar::valueOf),
                            utdanningGodkjent = rs.getString(UTDANNING_GODKJENT_NORGE)
                                ?.let(UtdanningGodkjentSvar::valueOf),
                            andreForhold = rs.getString(ANDRE_UTFORDRINGER)
                                ?.let(AndreForholdSvar::valueOf),
                        )
                    ))
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