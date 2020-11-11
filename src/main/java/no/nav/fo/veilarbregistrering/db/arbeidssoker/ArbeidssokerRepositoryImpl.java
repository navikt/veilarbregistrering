package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
import no.nav.fo.veilarbregistrering.arbeidssoker.EndretFormidlingsgruppeCommand;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.sbl.sql.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerperioderMapper.map;

public class ArbeidssokerRepositoryImpl implements ArbeidssokerRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ArbeidssokerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public long lagre(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {
        String personId = endretFormidlingsgruppeCommand.getPersonId();
        String formidlingsgruppe = endretFormidlingsgruppeCommand
                .getForrigeFormidlingsgruppe()
                .map(Formidlingsgruppe::stringValue)
                .orElse(null);
        Timestamp formidlingsgruppeEndret = Timestamp.valueOf(endretFormidlingsgruppeCommand.getFormidlingsgruppeEndret());

        // Lag en en funksjonell ID (hash) av tidspunkt, person_id og (ny) formidlingsgruppe
        String funksjonellID = opprettFunksjonellId(personId, formidlingsgruppe, formidlingsgruppeEndret.toString());

        // Sjekk om vi allerede har lagret meldingen
        if (erAlleredePersistentLagret(funksjonellID)) {
            return -1;
        }

        long id = nesteFraSekvens("FORMIDLINGSGRUPPE_SEQ");
        try {
            SqlUtils.insert(jdbcTemplate, "FORMIDLINGSGRUPPE")
                    .value("ID", id)
                    .value("FOEDSELSNUMMER", endretFormidlingsgruppeCommand.getFoedselsnummer()
                            .orElseThrow(() -> new IllegalStateException("Foedselsnummer var ikke satt. Skulle vært filtrert bort i forkant!"))
                            .stringValue())
                    .value("PERSON_ID", personId)
                    .value("PERSON_ID_STATUS", endretFormidlingsgruppeCommand.getPersonIdStatus())
                    .value("OPERASJON", endretFormidlingsgruppeCommand.getOperation().name())
                    .value("FORMIDLINGSGRUPPE", endretFormidlingsgruppeCommand.getFormidlingsgruppe().stringValue())
                    .value("FORMIDLINGSGRUPPE_ENDRET", formidlingsgruppeEndret)
                    .value("FORR_FORMIDLINGSGRUPPE", formidlingsgruppe)
                    .value("FORR_FORMIDLINGSGRUPPE_ENDRET", endretFormidlingsgruppeCommand.getForrigeFormidlingsgruppeEndret()
                            .map(Timestamp::valueOf)
                            .orElse(null))
                    .value("FORMIDLINGSGRUPPE_LEST", Timestamp.valueOf(LocalDateTime.now()))
                    .value("FUNKSJONELL_ID", funksjonellID)
                    .execute();
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    String.format("Lagring av følgende formidlingsgruppeendring feilet: %s", endretFormidlingsgruppeCommand),
                    e
            );
        }
        return id;
    }

    @SneakyThrows
    private String opprettFunksjonellId(String id, String gruppe, String endret) {
        String hashInput = new StringBuilder()
                .append(id)
                .append(gruppe)
                .append(endret)
                .toString();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(hashInput.getBytes());
        byte[] digest = md.digest();

      return DatatypeConverter.printHexBinary(digest).toUpperCase();

    }

    private boolean erAlleredePersistentLagret(String funksjonellID) {
        String sql = "SELECT * FROM FORMIDLINGSGRUPPE WHERE FUNKSJONELL_ID = ?";

        List<Formidlingsgruppeendring> formidlingsgruppeendringer = jdbcTemplate.query(
                sql,
                new Object[]{funksjonellID}, new FormidlingsgruppeendringRowMapper()
        );

        return !formidlingsgruppeendringer.isEmpty();
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return jdbcTemplate.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }

    @Override
    public Arbeidssokerperioder finnFormidlingsgrupper(Foedselsnummer foedselsnummer) {
        String sql = "SELECT * FROM FORMIDLINGSGRUPPE WHERE FOEDSELSNUMMER = ?";

        List<Formidlingsgruppeendring> formidlingsgruppeendringer = jdbcTemplate.query(
                sql,
                new Object[]{foedselsnummer.stringValue()}, new FormidlingsgruppeendringRowMapper()
        );

        return map(formidlingsgruppeendringer);
    }

    @Override
    public Arbeidssokerperioder finnFormidlingsgrupper(List<Foedselsnummer> listeMedFoedselsnummer) {
        String sql = "SELECT * FROM FORMIDLINGSGRUPPE WHERE FOEDSELSNUMMER IN (:foedselsnummer)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("foedselsnummer", listeMedFoedselsnummer.stream().map(f -> f.stringValue()).collect(Collectors.toList()));

        List<Formidlingsgruppeendring> formidlingsgruppeendringer = namedParameterJdbcTemplate.query(sql, parameters, new FormidlingsgruppeendringRowMapper());

        LOG.info(String.format("Fant følgende rådata med formidlingsgruppeendringer: %s", formidlingsgruppeendringer.toString()));

        return map(formidlingsgruppeendringer);
    }

    private class FormidlingsgruppeendringRowMapper implements RowMapper<Formidlingsgruppeendring> {

        @Override
        public Formidlingsgruppeendring mapRow(ResultSet rs, int i) throws SQLException {
            return new Formidlingsgruppeendring(
                    rs.getString("FORMIDLINGSGRUPPE"),
                    rs.getInt("PERSON_ID"),
                    rs.getString("PERSON_ID_STATUS"),
                    rs.getTimestamp("FORMIDLINGSGRUPPE_ENDRET"));
        }
    }
}
