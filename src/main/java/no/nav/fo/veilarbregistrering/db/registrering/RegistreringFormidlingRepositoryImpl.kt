package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidling
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidlingRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp

class RegistreringFormidlingRepositoryImpl(private val db: NamedParameterJdbcTemplate) : RegistreringFormidlingRepository {

    override fun lagre(registreringFormidling: RegistreringFormidling): Long {
        val id = nesteFraSekvens()
        val params = mapOf(
            "id" to id,
            "bruker_registrering_id" to registreringFormidling.brukerRegistreringId,
            "opprettet" to Timestamp.valueOf(registreringFormidling.opprettet),
            "sist_endret" to registreringFormidling.sistEndret?.let(Timestamp::valueOf),
            "status" to registreringFormidling.status.name
        )

        val sql = "INSERT INTO $REGISTRERING_TILSTAND" +
                " ($ID, $BRUKER_REGISTRERING_ID, $OPPRETTET, $SIST_ENDRET, $STATUS)" +
                " VALUES (:id, :bruker_registrering_id, :opprettet, :sist_endret, :status)"

        db.update(sql, params)
        return id
    }

    /**
     * Oppdaterer registreringtilstand, men sjekker samtidig etter oppdateringer som kan ha skjedd i parallell.
     * @param registreringFormidling
     * @throws IllegalStateException dersom sistEndret i databasen er nyere enn den vi forsøker å legge inn.
     */
    override fun oppdater(registreringFormidling: RegistreringFormidling): RegistreringFormidling {
        val original: RegistreringFormidling = hentRegistreringTilstand(registreringFormidling.id)
        if (original.sistEndret != null && original.sistEndret
                .isAfter(registreringFormidling.sistEndret)
        ) {
            throw IllegalStateException(
                "RegistreringTilstand hadde allerede blitt oppdatert " +
                        original.sistEndret.toString() + "Detaljer: " + registreringFormidling
            )
        }

        val params = mapOf(
            "status" to registreringFormidling.status.name,
            "sist_endret" to Timestamp.valueOf(registreringFormidling.sistEndret),
            "id" to registreringFormidling.id
        )

        val sql = "UPDATE $REGISTRERING_TILSTAND" +
                " SET $STATUS = :status, $SIST_ENDRET = :sist_endret" +
                " WHERE $ID = :id"

        db.update(sql, params)
        return hentRegistreringTilstand(registreringFormidling.id)
    }

    override fun hentRegistreringTilstand(id: Long): RegistreringFormidling {
        val sql = "SELECT * FROM $REGISTRERING_TILSTAND WHERE ID = :id"

        return db.queryForObject(sql, mapOf("id" to id), rowMapper())!!
    }

    override fun finnRegistreringTilstanderMed(status: Status): MutableList<RegistreringFormidling> {
        val sql = "SELECT * FROM $REGISTRERING_TILSTAND WHERE STATUS = :status"

        return db.query(sql, mapOf("status" to status.name), rowMapper())
    }


    override fun finnNesteRegistreringTilstandMed(status: Status): RegistreringFormidling? {
        val params = mapOf("status" to status.name)

        val sql = "SELECT * FROM $REGISTRERING_TILSTAND" +
                " WHERE $STATUS = :status" +
                " ORDER BY $OPPRETTET" +
                " FETCH NEXT 1 ROWS ONLY"

        return db.query(sql, params, rowMapper()).firstOrNull()
    }

    override fun hentAntallPerStatus(): Map<Status, Int> {
        val statusAntall = mutableMapOf<Status, Int>()
        Status.values().forEach { statusAntall[it] = 0 }

        val antallAlias = "antall"
        val sql = "SELECT $STATUS, COUNT(1) AS $antallAlias FROM $REGISTRERING_TILSTAND" +
                " GROUP BY $STATUS"

        db.query(sql) { rs ->
            val status = rs.getString(STATUS)
            val antall = rs.getInt(antallAlias)
            statusAntall[Status.valueOf(status)] = antall
        }

        return statusAntall
    }


    override fun hentTilstandFor(registreringsId: Long): RegistreringFormidling {
        val params = mapOf("bruker_registrering_id" to registreringsId)

        val sql = "SELECT * FROM $REGISTRERING_TILSTAND" +
                " WHERE $BRUKER_REGISTRERING_ID = :bruker_registrering_id" +
                " FETCH NEXT 1 ROWS ONLY"
        val registreringsTilstander: MutableList<RegistreringFormidling> =
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
        private fun rowMapper(): RowMapper<RegistreringFormidling> = RowMapper { rs, _ ->
            RegistreringFormidling.of(
                rs.getLong(ID),
                rs.getLong(BRUKER_REGISTRERING_ID),
                rs.getTimestamp(OPPRETTET).toLocalDateTime(),
                rs.getTimestamp(SIST_ENDRET)
                    ?.let(Timestamp::toLocalDateTime),
                Status.valueOf(rs.getString(STATUS))
            )
        }

        const val REGISTRERING_TILSTAND = "REGISTRERING_TILSTAND"
        const val SEQ_TABLE_NAME = "REGISTRERING_TILSTAND_SEQ"
        const val ID = "ID"
        const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        const val OPPRETTET = "OPPRETTET"
        const val SIST_ENDRET = "SIST_ENDRET"
        const val STATUS = "STATUS"
    }
}