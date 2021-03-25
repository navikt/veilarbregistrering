package no.nav.fo.veilarbregistrering.db.registrering

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.fo.veilarbregistrering.besvarelse.*
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.db.registrering.RegistreringTilstandRepositoryImpl.Companion.REGISTRERING_TILSTAND
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.TekstForSporsmal
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.IOException
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDateTime.now

class BrukerRegistreringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : BrukerRegistreringRepository {

    override fun lagre(registrering: OrdinaerBrukerRegistrering, bruker: Bruker): OrdinaerBrukerRegistrering {
        val id = nesteFraSekvens(BRUKER_REGISTRERING_SEQ)
        val besvarelse = registrering.besvarelse
        val stilling = registrering.sisteStilling
        val teksterForBesvarelse = tilJson(registrering.teksterForBesvarelse)

        val params = mapOf(
            "id" to id,
            "aktor_id" to bruker.aktorId.asString(),
            "fnr" to bruker.gjeldendeFoedselsnummer.stringValue(),
            "opprettet" to Timestamp.valueOf(registrering.opprettetDato ?: now()),
            "tekster" to teksterForBesvarelse,
            "yrkespraksis" to stilling.styrk08,
            "yrkesbeskrivelse" to stilling.label,
            "konsept_id" to stilling.konseptId,
            "begrunnelse" to besvarelse.dinSituasjon.toString(),
            "nus_kode" to UtdanningUtils.mapTilNuskode(besvarelse.utdanning),
            "utdanning_godkjent" to besvarelse.utdanningGodkjent.toString(),
            "utdanning_bestatt" to besvarelse.utdanningBestatt.toString(),
            "har_helseutfordringer" to besvarelse.helseHinder.toString(),
            "andre_utfordringer" to besvarelse.andreForhold.toString(),
            "jobbhistorikk" to besvarelse.sisteStilling.toString()
        )
        val sql = "INSERT INTO $BRUKER_REGISTRERING " +
                " (${allColumns.joinToString(", ")})" +
                " VALUES (:id, :aktor_id, :fnr, :opprettet, :tekster, :yrkespraksis, :yrkesbeskrivelse," +
                " :konsept_id, :begrunnelse, :nus_kode, :utdanning_godkjent, :utdanning_bestatt," +
                " :har_helseutfordringer, :andre_utfordringer, :jobbhistorikk)"

        db.update(sql, params)
        return hentBrukerregistreringForId(id)
    }


    override fun lagreSykmeldtBruker(bruker: SykmeldtRegistrering, aktorId: AktorId): Long {
        val id = nesteFraSekvens(SYKMELDT_REGISTRERING_SEQ)
        val besvarelse = bruker.besvarelse
        val teksterForBesvarelse = tilJson(bruker.teksterForBesvarelse)
        val params = mapOf(
            "id" to id,
            "aktor_id" to aktorId.asString(),
            "opprettet" to Timestamp.valueOf(now()),
            "tekster" to teksterForBesvarelse,
            "fremtidig_situasjon" to besvarelse.fremtidigSituasjon?.toString(),
            "tilbake_etter_52_uker" to besvarelse.tilbakeIArbeid?.toString(),
            "nus_kode" to UtdanningUtils.mapTilNuskode(besvarelse.utdanning),
            "utdanning_bestatt" to besvarelse.utdanningBestatt?.toString(),
            "utdanning_godkjent" to besvarelse.utdanningGodkjent?.toString(),
            "andre_utfordringer" to besvarelse.andreForhold?.toString()
        )

        val sql = "INSERT INTO $SYKMELDT_REGISTRERING" +
                " (${allSykmeldtColumns.joinToString(", ")})" +
                " VALUES (:id, :aktor_id, :opprettet, :tekster, :fremtidig_situasjon, :tilbake_etter_52_uker," +
                " :nus_kode, :utdanning_bestatt, :utdanning_godkjent, :andre_utfordringer)"

        db.update(sql, params)
        return id
    }

    override fun hentBrukerregistreringForId(brukerregistreringId: Long): OrdinaerBrukerRegistrering {
        val sql = "SELECT * FROM $BRUKER_REGISTRERING WHERE $BRUKER_REGISTRERING_ID = :id"

        return db.queryForObject(sql, mapOf("id" to brukerregistreringId), registreringMapper)!!
    }

    override fun hentOrdinaerBrukerregistreringForAktorIdOgTilstand(
        aktorId: AktorId,
        vararg tilstander: Status
    ): OrdinaerBrukerRegistrering? {
        val params = mapOf(
            "aktor_id" to aktorId.asString(),
            "tilstander" to tilstander.map { it.name }
        )

        val sql = "SELECT * FROM $BRUKER_REGISTRERING" +
                " LEFT JOIN $REGISTRERING_TILSTAND ON " +
                " $REGISTRERING_TILSTAND.${RegistreringTilstandRepositoryImpl.BRUKER_REGISTRERING_ID} = $BRUKER_REGISTRERING.$BRUKER_REGISTRERING_ID" +
                " WHERE $BRUKER_REGISTRERING.$AKTOR_ID = :aktor_id" +
                " AND $REGISTRERING_TILSTAND.${RegistreringTilstandRepositoryImpl.STATUS} in (:tilstander)" +
                " ORDER BY $BRUKER_REGISTRERING.$OPPRETTET_DATO DESC" +
                " FETCH NEXT 1 ROWS ONLY"

        return db.query(sql, params, registreringMapper).firstOrNull()
    }

    override fun hentOrdinaerBrukerregistreringForAktorId(aktorId: AktorId): OrdinaerBrukerRegistrering? {
        val sql = "SELECT * FROM $BRUKER_REGISTRERING" +
                " WHERE $AKTOR_ID = :aktor_id" +
                " ORDER BY $BRUKER_REGISTRERING_ID DESC" +
                " FETCH NEXT 1 ROWS ONLY"

        return db.query(sql, mapOf("aktor_id" to aktorId.asString()), registreringMapper).firstOrNull()
    }

    override fun hentSykmeldtregistreringForAktorId(aktorId: AktorId): SykmeldtRegistrering? {
        val sql = "SELECT * FROM $SYKMELDT_REGISTRERING" +
                " WHERE $AKTOR_ID = :aktor_id " +
                " ORDER BY $SYKMELDT_REGISTRERING_ID DESC" +
                " FETCH NEXT 1 ROWS ONLY"

        return db.query(sql, mapOf("aktor_id" to aktorId.asString()), rowMapperSykmeldt).firstOrNull()
    }

    override fun hentBrukerTilknyttet(brukerRegistreringId: Long): Bruker {
        val sql = "SELECT $FOEDSELSNUMMER, $AKTOR_ID FROM $BRUKER_REGISTRERING WHERE $BRUKER_REGISTRERING_ID = :id"

        return db.queryForObject(sql, mapOf("id" to brukerRegistreringId)) { rs, _ ->
            Bruker.of(
                Foedselsnummer.of(rs.getString("FOEDSELSNUMMER")),
                AktorId.of(rs.getString("AKTOR_ID"))
            )
        }!!
    }

    private fun nesteFraSekvens(sekvensNavn: String): Long {
        return db.queryForObject("SELECT $sekvensNavn.nextval FROM DUAL", noParams, Long::class.java)!!
    }

    companion object {
        private val mapper = jacksonObjectMapper()

        private const val BRUKER_REGISTRERING = "BRUKER_REGISTRERING"
        private const val SYKMELDT_REGISTRERING = "SYKMELDT_REGISTRERING"
        private const val BRUKER_REGISTRERING_SEQ = "BRUKER_REGISTRERING_SEQ"
        private const val SYKMELDT_REGISTRERING_SEQ = "SYKMELDT_REGISTRERING_SEQ"
        const val SYKMELDT_REGISTRERING_ID = "SYKMELDT_REGISTRERING_ID"
        private const val FREMTIDIG_SITUASJON = "FREMTIDIG_SITUASJON"
        private const val TILBAKE_ETTER_52_UKER = "TILBAKE_ETTER_52_UKER"
        const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        const val OPPRETTET_DATO = "OPPRETTET_DATO"
        const val NUS_KODE = "NUS_KODE"
        const val YRKESPRAKSIS = "YRKESPRAKSIS"
        private const val HAR_HELSEUTFORDRINGER = "HAR_HELSEUTFORDRINGER"
        const val YRKESBESKRIVELSE = "YRKESBESKRIVELSE"
        const val KONSEPT_ID = "KONSEPT_ID"
        private const val TEKSTER_FOR_BESVARELSE = "TEKSTER_FOR_BESVARELSE"
        const val ANDRE_UTFORDRINGER = "ANDRE_UTFORDRINGER"
        const val BEGRUNNELSE_FOR_REGISTRERING = "BEGRUNNELSE_FOR_REGISTRERING"
        const val UTDANNING_BESTATT = "UTDANNING_BESTATT"
        const val UTDANNING_GODKJENT_NORGE = "UTDANNING_GODKJENT_NORGE"
        private const val JOBBHISTORIKK = "JOBBHISTORIKK"
        private const val FOEDSELSNUMMER = "FOEDSELSNUMMER"
        const val AKTOR_ID = "AKTOR_ID"

        private val allColumns = listOf(
            BRUKER_REGISTRERING_ID,
            AKTOR_ID,
            FOEDSELSNUMMER,
            OPPRETTET_DATO,
            TEKSTER_FOR_BESVARELSE,
            YRKESPRAKSIS,
            YRKESBESKRIVELSE,
            KONSEPT_ID,
            BEGRUNNELSE_FOR_REGISTRERING,
            NUS_KODE,
            UTDANNING_GODKJENT_NORGE,
            UTDANNING_BESTATT,
            HAR_HELSEUTFORDRINGER,
            ANDRE_UTFORDRINGER,
            JOBBHISTORIKK
        )
        private val allSykmeldtColumns = listOf(
            SYKMELDT_REGISTRERING_ID,
            AKTOR_ID,
            OPPRETTET_DATO,
            TEKSTER_FOR_BESVARELSE,
            FREMTIDIG_SITUASJON,
            TILBAKE_ETTER_52_UKER,
            NUS_KODE,
            UTDANNING_BESTATT,
            UTDANNING_GODKJENT_NORGE,
            ANDRE_UTFORDRINGER
        )
        private val registreringMapper = RowMapper<OrdinaerBrukerRegistrering> { rs, _ ->
            try {
                OrdinaerBrukerRegistrering()
                    .setId(rs.getLong(BRUKER_REGISTRERING_ID))
                    .setOpprettetDato(rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime())
                    .setTeksterForBesvarelse(readListOf(rs.getString(TEKSTER_FOR_BESVARELSE)))
                    .setSisteStilling(
                        Stilling()
                            .setStyrk08(rs.getString(YRKESPRAKSIS))
                            .setKonseptId(rs.getLong(KONSEPT_ID))
                            .setLabel(rs.getString(YRKESBESKRIVELSE))
                    )
                    .setBesvarelse(
                        Besvarelse()
                            .setDinSituasjon(DinSituasjonSvar.valueOf(rs.getString(BEGRUNNELSE_FOR_REGISTRERING)))
                            .setUtdanning(UtdanningUtils.mapTilUtdanning(rs.getString(NUS_KODE)))
                            .setUtdanningBestatt(UtdanningBestattSvar.valueOf(rs.getString(UTDANNING_BESTATT)))
                            .setUtdanningGodkjent(
                                UtdanningGodkjentSvar.valueOf(
                                    rs.getString(
                                        UTDANNING_GODKJENT_NORGE
                                    )
                                )
                            )
                            .setHelseHinder(HelseHinderSvar.valueOf(rs.getString(HAR_HELSEUTFORDRINGER)))
                            .setAndreForhold(AndreForholdSvar.valueOf(rs.getString(ANDRE_UTFORDRINGER)))
                            .setSisteStilling(SisteStillingSvar.valueOf(rs.getString(JOBBHISTORIKK)))
                    )
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }

        private val rowMapperSykmeldt = RowMapper { rs, _ ->
                try {
                    SykmeldtRegistrering()
                        .setId(rs.getLong(SYKMELDT_REGISTRERING_ID))
                        .setOpprettetDato(rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime())
                        .setTeksterForBesvarelse(readListOf(rs.getString(TEKSTER_FOR_BESVARELSE)))
                        .setBesvarelse(
                            Besvarelse()
                                .setFremtidigSituasjon(rs.getString(FREMTIDIG_SITUASJON)?.let(FremtidigSituasjonSvar::valueOf))
                                .setTilbakeIArbeid(rs.getString(TILBAKE_ETTER_52_UKER)?.let(TilbakeIArbeidSvar::valueOf))
                                .setUtdanning(UtdanningUtils.mapTilUtdanning(rs.getString(NUS_KODE)))
                                .setUtdanningBestatt(rs.getString(UTDANNING_BESTATT)?.let(UtdanningBestattSvar::valueOf))
                                .setUtdanningGodkjent(rs.getString(UTDANNING_GODKJENT_NORGE)?.let(UtdanningGodkjentSvar::valueOf))
                                .setAndreForhold(rs.getString(ANDRE_UTFORDRINGER)?.let(AndreForholdSvar::valueOf))
                        )
                } catch (e: SQLException) {
                    throw RuntimeException(e)
                }
            }

        private val noParams = emptyMap<String, Any>()

        private fun tilJson(obj: List<TekstForSporsmal>): String =
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
    }
}