package no.nav.fo.veilarbregistrering.registrering.ordinaer.db

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.fo.veilarbregistrering.besvarelse.AndreForholdSvar
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar
import no.nav.fo.veilarbregistrering.besvarelse.SisteStillingSvar
import no.nav.fo.veilarbregistrering.besvarelse.Stilling
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningBestattSvar
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningGodkjentSvar
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningSvar
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningUtils
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.bruker.TekstForSporsmal
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.formidling.db.RegistreringTilstandRepositoryImpl
import no.nav.fo.veilarbregistrering.registrering.formidling.db.RegistreringTilstandRepositoryImpl.Companion.REGISTRERING_TILSTAND
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.paw.arbeidssokerregisteret.intern.v1.OpplysningerOmArbeidssoekerMottatt
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Annet
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Arbeidserfaring
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.BrukerType
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Helse
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.JaNeiVetIkke
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Jobbsituasjon
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.JobbsituasjonBeskrivelse
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.JobbsituasjonMedDetaljer
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Metadata
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.OpplysningerOmArbeidssoeker
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Utdanning
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Utdanningsnivaa
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.IOException
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*

class BrukerRegistreringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : BrukerRegistreringRepository {

    override fun lagre(registrering: OrdinaerBrukerRegistrering, bruker: Bruker): OrdinaerBrukerRegistrering {
        val id = nesteFraSekvens(BRUKER_REGISTRERING_SEQ)
        val besvarelse = registrering.besvarelse
        val stilling = registrering.sisteStilling
        val teksterForBesvarelse = tilJson(registrering.teksterForBesvarelse)

        val params = mapOf(
            "id" to id,
            "aktor_id" to bruker.aktorId.aktorId,
            "fnr" to bruker.gjeldendeFoedselsnummer.stringValue(),
            "opprettet" to Timestamp.valueOf(registrering.opprettetDato),
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

    override fun hentBrukerregistreringForId(brukerregistreringId: Long): OrdinaerBrukerRegistrering {
        val sql = "SELECT * FROM $BRUKER_REGISTRERING WHERE $BRUKER_REGISTRERING_ID = :id"

        return db.queryForObject(sql, mapOf("id" to brukerregistreringId), registreringMapper)!!
    }

    override fun hentBrukerregistreringForFoedselsnummer(foedselsnummerList: List<Foedselsnummer>): List<OrdinaerBrukerRegistrering> {
        val sql = "SELECT * FROM $BRUKER_REGISTRERING" +
                " LEFT JOIN $REGISTRERING_TILSTAND ON" +
                " $REGISTRERING_TILSTAND.${RegistreringTilstandRepositoryImpl.BRUKER_REGISTRERING_ID} = $BRUKER_REGISTRERING.$BRUKER_REGISTRERING_ID" +
                " WHERE $FOEDSELSNUMMER IN (:foedselsnummer)" +
                " AND $REGISTRERING_TILSTAND.${RegistreringTilstandRepositoryImpl.STATUS} in (:tilstander)"

        val parameters = mapOf(
            "foedselsnummer" to foedselsnummerList.map(Foedselsnummer::stringValue),
            "tilstander" to Status.gyldigTilstand().map { it.name }
        )

        return db.query(sql, parameters, registreringMapper)
    }

    override fun finnOrdinaerBrukerregistreringForAktorIdOgTilstand(
        aktorId: AktorId,
        tilstander: List<Status>
    ): List<OrdinaerBrukerRegistrering> {
        val params = mapOf(
            "aktor_id" to aktorId.aktorId,
            "tilstander" to tilstander.map { it.name }
        )

        val sql = "SELECT * FROM $BRUKER_REGISTRERING" +
                " LEFT JOIN $REGISTRERING_TILSTAND ON " +
                " $REGISTRERING_TILSTAND.${RegistreringTilstandRepositoryImpl.BRUKER_REGISTRERING_ID} = $BRUKER_REGISTRERING.$BRUKER_REGISTRERING_ID" +
                " WHERE $BRUKER_REGISTRERING.$AKTOR_ID = :aktor_id" +
                " AND $REGISTRERING_TILSTAND.${RegistreringTilstandRepositoryImpl.STATUS} in (:tilstander)" +
                " ORDER BY $BRUKER_REGISTRERING.$OPPRETTET_DATO DESC"

        return db.query(sql, params, registreringMapper)
    }

    override fun hentBrukerTilknyttet(brukerRegistreringId: Long): Bruker {
        val sql = "SELECT $FOEDSELSNUMMER, $AKTOR_ID FROM $BRUKER_REGISTRERING WHERE $BRUKER_REGISTRERING_ID = :id"

        return db.queryForObject(sql, mapOf("id" to brukerRegistreringId)) { rs, _ ->
            Bruker(
                Foedselsnummer(rs.getString("FOEDSELSNUMMER")),
                AktorId(rs.getString("AKTOR_ID"))
            )
        }!!
    }

    override fun hentNesteOpplysningerOmArbeidssoeker(antall: Int): List<Pair<Long, OpplysningerOmArbeidssoekerMottatt>> {
        val sql = """
            SELECT bruker_registrering.* FROM $BRUKER_REGISTRERING
            JOIN registrering_tilstand ON bruker_registrering.bruker_registrering_id = registrering_tilstand.bruker_registrering_id
            WHERE registrering_tilstand.status in ('PUBLISERT_KAFKA','OPPRINNELIG_OPPRETTET_UTEN_TILSTAND')
            AND overfort_kafka IS FALSE
            AND $FOEDSELSNUMMER IS NOT NULL
            ORDER BY $OPPRETTET_DATO LIMIT $antall
        """.trimIndent()

        return db.query(sql, noParams, opplysningerOmArbeidssoekerMapper)
    }

    override fun settOpplysningerOmArbeidssoekerSomOverfort(listeMedIder: List<Int>) {
        val sql =
            "UPDATE $BRUKER_REGISTRERING SET overfort_kafka = TRUE WHERE $BRUKER_REGISTRERING_ID IN (:listeMedIder)"
        val params = mapOf("listeMedIder" to listeMedIder)

        db.update(sql, params)
    }


    private fun nesteFraSekvens(sekvensNavn: String): Long {
        val sql = "SELECT nextVal('$sekvensNavn')"
        return db.queryForObject(sql, noParams, Long::class.java)!!
    }

    companion object {
        private val mapper = jacksonObjectMapper()

        private const val BRUKER_REGISTRERING = "BRUKER_REGISTRERING"
        private const val BRUKER_REGISTRERING_SEQ = "BRUKER_REGISTRERING_SEQ"
        const val FREMTIDIG_SITUASJON = "FREMTIDIG_SITUASJON"
        const val TILBAKE_ETTER_52_UKER = "TILBAKE_ETTER_52_UKER"
        const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        const val OPPRETTET_DATO = "OPPRETTET_DATO"
        const val NUS_KODE = "NUS_KODE"
        const val YRKESPRAKSIS = "YRKESPRAKSIS"
        private const val HAR_HELSEUTFORDRINGER = "HAR_HELSEUTFORDRINGER"
        const val YRKESBESKRIVELSE = "YRKESBESKRIVELSE"
        const val KONSEPT_ID = "KONSEPT_ID"
        const val TEKSTER_FOR_BESVARELSE = "TEKSTER_FOR_BESVARELSE"
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

        private val registreringMapper = RowMapper<OrdinaerBrukerRegistrering> { rs, _ ->
            try {
                OrdinaerBrukerRegistrering(
                    id = rs.getLong(BRUKER_REGISTRERING_ID),
                    opprettetDato = rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime(),
                    teksterForBesvarelse = readListOf(rs.getString(TEKSTER_FOR_BESVARELSE)),
                    sisteStilling =
                    Stilling(
                        styrk08 = rs.getString(YRKESPRAKSIS),
                        konseptId = rs.getLong(KONSEPT_ID),
                        label = rs.getString(YRKESBESKRIVELSE)
                    ),
                    besvarelse = (
                            Besvarelse(
                                dinSituasjon = DinSituasjonSvar.valueOf(rs.getString(BEGRUNNELSE_FOR_REGISTRERING)),
                                utdanning = UtdanningUtils.mapTilUtdanning(rs.getString(NUS_KODE)),
                                utdanningBestatt = UtdanningBestattSvar.valueOf(rs.getString(UTDANNING_BESTATT)),
                                utdanningGodkjent = UtdanningGodkjentSvar.valueOf(rs.getString(UTDANNING_GODKJENT_NORGE)),
                                helseHinder = HelseHinderSvar.valueOf(rs.getString(HAR_HELSEUTFORDRINGER)),
                                andreForhold = AndreForholdSvar.valueOf(rs.getString(ANDRE_UTFORDRINGER)),
                                sisteStilling = SisteStillingSvar.valueOf(rs.getString(JOBBHISTORIKK)),
                            )
                            ),
                )
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }

        val opplysningerOmArbeidssoekerMapper = RowMapper<Pair<Long, OpplysningerOmArbeidssoekerMottatt>> { rs, _ ->
            rs.getLong(BRUKER_REGISTRERING_ID) to OpplysningerOmArbeidssoekerMottatt(
                hendelseId = UUID.randomUUID(),
                identitetsnummer = rs.getString(FOEDSELSNUMMER),
                opplysningerOmArbeidssoeker = OpplysningerOmArbeidssoeker(
                    id = UUID.randomUUID(),
                    metadata = Metadata(
                        tidspunkt = rs.getTimestamp(OPPRETTET_DATO).toInstant(),
                        utfoertAv = no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Bruker(
                            type = BrukerType.SLUTTBRUKER,
                            id = rs.getString(FOEDSELSNUMMER)
                        ),
                        kilde = "veilarbregistrering",
                        aarsak = "registrering"
                    ),
                    utdanning = Utdanning(
                        nus = rs.getString(NUS_KODE),
                        bestaatt = when (UtdanningBestattSvar.valueOf(rs.getString(UTDANNING_BESTATT))) {
                            UtdanningBestattSvar.JA -> JaNeiVetIkke.JA
                            UtdanningBestattSvar.NEI -> JaNeiVetIkke.NEI
                            else -> JaNeiVetIkke.VET_IKKE
                        },
                        godkjent = when (UtdanningGodkjentSvar.valueOf(rs.getString(UTDANNING_GODKJENT_NORGE))) {
                            UtdanningGodkjentSvar.JA -> JaNeiVetIkke.JA
                            UtdanningGodkjentSvar.NEI -> JaNeiVetIkke.NEI
                            UtdanningGodkjentSvar.VET_IKKE -> JaNeiVetIkke.VET_IKKE
                            UtdanningGodkjentSvar.INGEN_SVAR -> JaNeiVetIkke.VET_IKKE
                        }
                    ),
                    arbeidserfaring = Arbeidserfaring(
                        harHattArbeid = when (SisteStillingSvar.valueOf(rs.getString(JOBBHISTORIKK))) {
                            SisteStillingSvar.HAR_HATT_JOBB -> JaNeiVetIkke.JA
                            SisteStillingSvar.HAR_IKKE_HATT_JOBB -> JaNeiVetIkke.NEI
                            SisteStillingSvar.INGEN_SVAR -> JaNeiVetIkke.VET_IKKE
                        }
                    ),
                    jobbsituasjon = Jobbsituasjon(
                        beskrivelser = listOfNotNull(
                            when (DinSituasjonSvar.valueOf(rs.getString(BEGRUNNELSE_FOR_REGISTRERING))) {
                                DinSituasjonSvar.MISTET_JOBBEN -> JobbsituasjonBeskrivelse.HAR_BLITT_SAGT_OPP
                                DinSituasjonSvar.ALDRI_HATT_JOBB -> JobbsituasjonBeskrivelse.ALDRI_HATT_JOBB
                                DinSituasjonSvar.HAR_SAGT_OPP -> JobbsituasjonBeskrivelse.HAR_SAGT_OPP
                                DinSituasjonSvar.VIL_BYTTE_JOBB -> JobbsituasjonBeskrivelse.VIL_BYTTE_JOBB
                                DinSituasjonSvar.ER_PERMITTERT -> JobbsituasjonBeskrivelse.ER_PERMITTERT
                                DinSituasjonSvar.USIKKER_JOBBSITUASJON -> JobbsituasjonBeskrivelse.USIKKER_JOBBSITUASJON
                                DinSituasjonSvar.JOBB_OVER_2_AAR -> JobbsituasjonBeskrivelse.IKKE_VAERT_I_JOBB_SISTE_2_AAR
                                DinSituasjonSvar.VIL_FORTSETTE_I_JOBB -> null
                                DinSituasjonSvar.AKKURAT_FULLFORT_UTDANNING -> JobbsituasjonBeskrivelse.AKKURAT_FULLFORT_UTDANNING
                                DinSituasjonSvar.DELTIDSJOBB_VIL_MER -> JobbsituasjonBeskrivelse.DELTIDSJOBB_VIL_MER
                            }?.let { beskrivelse ->
                                JobbsituasjonMedDetaljer(
                                    beskrivelse = beskrivelse,
                                    detaljer = rs.getString(YRKESPRAKSIS)
                                        ?.takeIf { it.isNotBlank() && it != "X" && it != "-1" }
                                        ?.let {
                                            mapOf("stilling_styrk08" to it)
                                        } ?: emptyMap()
                                )
                            }
                        )
                    ),
                    annet = Annet(
                        andreForholdHindrerArbeid = when (AndreForholdSvar.valueOf(rs.getString(ANDRE_UTFORDRINGER))) {
                            AndreForholdSvar.JA -> JaNeiVetIkke.JA
                            AndreForholdSvar.NEI -> JaNeiVetIkke.NEI
                            AndreForholdSvar.INGEN_SVAR -> JaNeiVetIkke.VET_IKKE
                        }
                    ),
                    helse = Helse(
                        helsetilstandHindrerArbeid = when (HelseHinderSvar.valueOf(rs.getString(HAR_HELSEUTFORDRINGER))) {
                            HelseHinderSvar.JA -> JaNeiVetIkke.JA
                            HelseHinderSvar.NEI -> JaNeiVetIkke.NEI
                            HelseHinderSvar.INGEN_SVAR -> JaNeiVetIkke.VET_IKKE
                        }
                    )
                )
            )
        }

        private val noParams = emptyMap<String, Any>()

        private fun tilJson(obj: List<TekstForSporsmal>): String =
            try {
                ObjectMapper().writeValueAsString(obj)
            } catch (e: JsonProcessingException) {
                "[]"
            }

        private inline fun <reified T> readListOf(json: String?): List<T> =
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