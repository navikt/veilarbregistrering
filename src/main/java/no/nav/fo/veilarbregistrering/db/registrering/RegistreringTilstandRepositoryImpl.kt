package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp

class RegistreringTilstandRepositoryImpl(private val db: NamedParameterJdbcTemplate) : RegistreringTilstandRepository {

    override fun lagre(registreringTilstand: RegistreringTilstand): Long {
        val id = nesteFraSekvens()
        val params = mapOf(
            "id" to id,
            "bruker_registrering_id" to registreringTilstand.brukerRegistreringId,
            "opprettet" to registreringTilstand.opprettet,
            "sist_endret" to registreringTilstand.sistEndret,
            "status" to registreringTilstand.status.name
        )

        val sql = "INSERT INTO $TABLE_NAME" +
                " ($ID, $BRUKER_REGISTRERING_ID, $OPPRETTET, $SIST_ENDRET, $STATUS)" +
                " VALUES (:id, :bruker_registrering_id, :opprettet, :sist_endret, :status);"

        db.update(sql, params)
        return id
    }

    /**
     * Oppdaterer registreringtilstand, men sjekker samtidig etter oppdateringer som kan ha skjedd i parallell.
     * @param registreringTilstand
     * @throws IllegalStateException dersom sistEndret i databasen er nyere enn den vi forsøker å legge inn.
     */
    override fun oppdater(registreringTilstand: RegistreringTilstand): RegistreringTilstand {
        val original: RegistreringTilstand = hentRegistreringTilstand(registreringTilstand.id)
        if (original.sistEndret != null && original.sistEndret
                .isAfter(registreringTilstand.sistEndret)
        ) {
            throw IllegalStateException(
                "RegistreringTilstand hadde allerede blitt oppdatert " +
                        original.sistEndret.toString() + "Detaljer: " + registreringTilstand
            )
        }

        val params = mapOf(
            "status" to registreringTilstand.status.name,
            "sist_endret" to registreringTilstand.sistEndret,
            "id" to registreringTilstand.id
        )

        val sql = "UPDATE $TABLE_NAME" +
                " SET $STATUS = :status, $SIST_ENDRET = :sist_endret" +
                " WHERE $ID = :id" +
                " "

        db.update(sql, params)
        return hentRegistreringTilstand(registreringTilstand.id)
    }

    override fun hentRegistreringTilstand(id: Long): RegistreringTilstand {
        val sql = "SELECT * FROM $TABLE_NAME WHERE ID = :id"

        return db.queryForObject(sql, mapOf("id" to id), rowMapper())!!
    }

    override fun finnRegistreringTilstanderMed(status: Status): MutableList<RegistreringTilstand> {
        val sql = "SELECT * FROM $TABLE_NAME WHERE STATUS = :status"

        return db.query(sql, mapOf("status" to status.name), rowMapper())
    }


    override fun finnNesteRegistreringTilstandMed(status: Status): RegistreringTilstand? {
        val params = mapOf("status" to status.name)

        val sql = "SELECT * FROM $TABLE_NAME" +
                " WHERE $STATUS = :status" +
                " ORDER BY $OPPRETTET" +
                " FETCH NEXT 1 ROWS ONLY"

        return db.query(sql, params, rowMapper()).firstOrNull()
    }

    override fun hentAntall(status: Status): Int {
        val params = mapOf("status" to status.name)

        val sql = "SELECT COUNT(1) FROM $TABLE_NAME" +
                " WHERE $STATUS = :status"

        return db.queryForObject(sql, params, Int::class.java)!!
    }


    override fun hentTilstandFor(registreringsId: Long): RegistreringTilstand {
        val params = mapOf("bruker_registrering_id" to registreringsId)

        val sql = "SELECT * FROM $TABLE_NAME" +
                " WHERE $BRUKER_REGISTRERING_ID = :bruker_registrering_id" +
                " FETCH NEXT 1 ROWS ONLY"
        val registreringsTilstander: MutableList<RegistreringTilstand> =
            db.query(sql, params, rowMapper())
        return registreringsTilstander.stream().findFirst()
            .orElseThrow { IllegalStateException("Registrering med id $registreringsId mangler tilstand") }
    }

    private fun nesteFraSekvens(): Long {
        return db.queryForObject(
            "select $SEQ_TABLE_NAME.nextval from dual",
            emptyMap<String, Any>(),
            Long::class.java
        )!!
    }

    companion object {
        private fun rowMapper(): RowMapper<RegistreringTilstand> = RowMapper { rs, _ ->
            RegistreringTilstand.of(
                rs.getLong(ID),
                rs.getLong(BRUKER_REGISTRERING_ID),
                rs.getTimestamp(OPPRETTET).toLocalDateTime(),
                rs.getTimestamp(SIST_ENDRET)
                    ?.let(Timestamp::toLocalDateTime),
                Status.valueOf(rs.getString(STATUS))
            )
        }

        const val TABLE_NAME = "REGISTRERING_TILSTAND"
        const val SEQ_TABLE_NAME = "REGISTRERING_TILSTAND_SEQ"
        const val ID = "ID"
        const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        const val OPPRETTET = "OPPRETTET"
        const val SIST_ENDRET = "SIST_ENDRET"
        const val STATUS = "STATUS"
    }
}