package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder;
import no.nav.fo.veilarbregistrering.arbeidssoker.EndretFormidlingsgruppeCommand;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import no.nav.sbl.sql.SqlUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerperioderMapper.map;

public class ArbeidssokerRepositoryImpl implements ArbeidssokerRepository {

    private final JdbcTemplate jdbcTemplate;

    public ArbeidssokerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long lagre(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {
        long id = nesteFraSekvens("FORMIDLINGSGRUPPE_SEQ");
        SqlUtils.insert(jdbcTemplate, "FORMIDLINGSGRUPPE")
                .value("ID", id)
                .value("FOEDSELSNUMMER", endretFormidlingsgruppeCommand.getFoedselsnummer()
                        .orElseThrow(() -> new IllegalStateException("Foedselsnummer var ikke satt. Skulle vært filtrert bort i forkant!"))
                        .stringValue())
                .value("PERSON_ID", endretFormidlingsgruppeCommand.getPersonId())
                .value("OPERASJON", endretFormidlingsgruppeCommand.getOperation().name())
                .value("FORMIDLINGSGRUPPE", endretFormidlingsgruppeCommand.getFormidlingsgruppe().stringValue())
                .value("FORMIDLINGSGRUPPE_ENDRET", Timestamp.valueOf(endretFormidlingsgruppeCommand.getFormidlingsgruppeEndret()))
                .value("FORR_FORMIDLINGSGRUPPE", endretFormidlingsgruppeCommand.getForrigeFormidlingsgruppe()
                        .map(Formidlingsgruppe::stringValue)
                        .orElse(null))
                .value("FORR_FORMIDLINGSGRUPPE_ENDRET", endretFormidlingsgruppeCommand.getForrigeFormidlingsgruppeEndret()
                        .map(Timestamp::valueOf)
                       .orElse(null))
                .value("FORMIDLINGSGRUPPE_LEST", Timestamp.valueOf(LocalDateTime.now()))
                .execute();

        return id;
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return jdbcTemplate.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }

    @Override
    public Arbeidssokerperioder finnFormidlingsgrupper(Foedselsnummer foedselsnummer) {
        String sql = "SELECT * FROM FORMIDLINGSGRUPPE WHERE FOEDSELSNUMMER = ?";

        List<ArbeidssokerperiodeRaaData> arbeidssokerperioder = jdbcTemplate.query(
                sql,
                new Object[]{foedselsnummer.stringValue()},
                (rs, row) -> new ArbeidssokerperiodeRaaData(
                        rs.getString("FORMIDLINGSGRUPPE"),
                        rs.getTimestamp("FORMIDLINGSGRUPPE_ENDRET")
                )
        );

        return map(arbeidssokerperioder);
    }
}
