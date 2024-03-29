package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.db

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Operation
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class FormidlingsgruppeRepositoryImpl(private val db: NamedParameterJdbcTemplate) : FormidlingsgruppeRepository {

    override fun lagre(event: FormidlingsgruppeEndretEvent): Long {
        val personId = event.personId
        val formidlingsgruppe = event.formidlingsgruppe.kode
        val formidlingsgruppeEndret = Timestamp.valueOf(event.formidlingsgruppeEndret.truncatedTo(ChronoUnit.MICROS))

        if (erAlleredePersistentLagret(personId, formidlingsgruppe, formidlingsgruppeEndret)) {
            logger.info(
                "Endringen er allerede lagret, denne forkastes. PersonID:" +
                        " $personId, Formidlingsgruppe: $formidlingsgruppe, Endret: $formidlingsgruppeEndret"
            )
            return -1
        }

        val id = nesteFraSekvens()
        val params = mapOf(
            "id" to id,
            "fnr" to event.foedselsnummer.stringValue(),
            "person_id" to personId,
            "person_id_status" to event.personIdStatus,
            "operasjon" to event.operation.name,
            "formidlingsgruppe" to formidlingsgruppe,
            "formidlingsgruppe_endret" to formidlingsgruppeEndret,
            "forrige_formidlingsgruppe" to event.forrigeFormidlingsgruppe?.let(Formidlingsgruppe::kode),
            "forrige_formidlingsgruppe_endret" to event.forrigeFormidlingsgruppeEndret?.let(Timestamp::valueOf),
            "formidlingsgruppe_lest" to Timestamp.valueOf(LocalDateTime.now())
        )
        val sql = "INSERT INTO $FORMIDLINGSGRUPPE ($ID, $FOEDSELSNUMMER, $PERSON_ID, $PERSON_ID_STATUS, $OPERASJON," +
                " $FORMIDLINGSGRUPPE, $FORMIDLINGSGRUPPE_ENDRET, $FORR_FORMIDLINGSGRUPPE," +
                " $FORR_FORMIDLINGSGRUPPE_ENDRET, $FORMIDLINGSGRUPPE_LEST)" +
                " VALUES (:id, :fnr, :person_id, :person_id_status, :operasjon, :formidlingsgruppe, " +
                " :formidlingsgruppe_endret, :forrige_formidlingsgruppe, :forrige_formidlingsgruppe_endret," +
                " :formidlingsgruppe_lest)"
        try {
            db.update(sql, params)
        } catch (e: DataIntegrityViolationException) {
            throw DataIntegrityViolationException("Lagring av følgende formidlingsgruppeendring feilet: $event", e)
        }
        return id
    }

    private fun erAlleredePersistentLagret(personID: String, formidlingsgruppe: String, endret: Timestamp): Boolean {
        val params = mapOf("person_id" to personID, "formidlingsgruppe" to formidlingsgruppe, "endret" to endret)

        val sql = "SELECT * FROM $FORMIDLINGSGRUPPE " +
                " WHERE $PERSON_ID = :person_id " +
                " AND $FORMIDLINGSGRUPPE = :formidlingsgruppe " +
                " AND $FORMIDLINGSGRUPPE_ENDRET = :endret"

        val formidlingsgruppeendringer = db.query(sql, params, fgruppeEndretEvent)
        return formidlingsgruppeendringer.isNotEmpty()
    }

    private fun nesteFraSekvens(): Long {
        val sql = "SELECT nextVal('$FORMIDLINGSGRUPPE_SEQ')"
        return db.queryForObject(sql, emptyMap<String, Any>(), Long::class.java)!!
    }

    override fun finnFormidlingsgruppeEndretEventFor(foedselsnummerList: List<Foedselsnummer>): List<FormidlingsgruppeEndretEvent> {
        val sql = "SELECT * FROM $FORMIDLINGSGRUPPE WHERE $FOEDSELSNUMMER IN (:foedselsnummer)"
        val parameters = mapOf("foedselsnummer" to foedselsnummerList.map(Foedselsnummer::stringValue))

        val formidlingsgruppeendringer = db.query(sql, parameters, fgruppeEndretEvent)

        logger.info(String.format("Fant følgende rådata med formidlingsgruppeendringer: %s", formidlingsgruppeendringer.toString()))
        return formidlingsgruppeendringer
    }

    override fun hentDistinkteFnrForArbeidssokere(): List<Foedselsnummer> {
        val sql = "SELECT DISTINCT $FOEDSELSNUMMER FROM $FORMIDLINGSGRUPPE WHERE $FORMIDLINGSGRUPPE = 'ARBS'"

        return db.query(sql) { rs, _ -> Foedselsnummer(rs.getString(FOEDSELSNUMMER)) }
    }

    override fun hentFoedselsnummerForPersonId(personId: String): List<Foedselsnummer> {
        val sql = "SELECT $FOEDSELSNUMMER FROM $FORMIDLINGSGRUPPE WHERE $PERSON_ID = :personId"
        val params = mapOf("personId" to personId)

        return db.query(sql, params) { rs, _ -> Foedselsnummer(rs.getString(FOEDSELSNUMMER)) }
    }

    companion object {
        const val FORMIDLINGSGRUPPE_SEQ = "FORMIDLINGSGRUPPE_SEQ"
        const val FORMIDLINGSGRUPPE = "FORMIDLINGSGRUPPE"
        const val ID = "ID"
        const val FOEDSELSNUMMER = "FOEDSELSNUMMER"
        const val PERSON_ID = "PERSON_ID"
        const val PERSON_ID_STATUS = "PERSON_ID_STATUS"
        const val OPERASJON = "OPERASJON"
        private const val FORMIDLINGSGRUPPE_ENDRET = "FORMIDLINGSGRUPPE_ENDRET"
        private const val FORR_FORMIDLINGSGRUPPE = "FORR_FORMIDLINGSGRUPPE"
        private const val FORR_FORMIDLINGSGRUPPE_ENDRET = "FORR_FORMIDLINGSGRUPPE_ENDRET"
        private const val FORMIDLINGSGRUPPE_LEST = "FORMIDLINGSGRUPPE_LEST"

        private val fgruppeEndretEvent = RowMapper { rs, _ ->
            FormidlingsgruppeEndretEvent(
                Foedselsnummer(rs.getString(FOEDSELSNUMMER)),
                rs.getString(PERSON_ID),
                rs.getString(PERSON_ID_STATUS),
                Operation.valueOf(rs.getString(OPERASJON)),
                Formidlingsgruppe(rs.getString(FORMIDLINGSGRUPPE)),
                rs.getTimestamp(FORMIDLINGSGRUPPE_ENDRET).toLocalDateTime(),
                rs.getString(FORR_FORMIDLINGSGRUPPE)?.let { Formidlingsgruppe(it) },
                rs.getTimestamp(FORR_FORMIDLINGSGRUPPE_ENDRET)?.toLocalDateTime(),
            )
        }
    }
}