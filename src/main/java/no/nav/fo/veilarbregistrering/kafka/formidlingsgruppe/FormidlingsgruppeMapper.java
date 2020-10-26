package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Optional.ofNullable;

public abstract class FormidlingsgruppeMapper {

    private static final Gson GSON = new Gson();

    public static FormidlingsgruppeEvent map(String record) {
        GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto = GSON.fromJson(record, GgArenaFormidlinggruppeDto.class);
        return getInstance(ggArenaFormidlinggruppeDto).map(ggArenaFormidlinggruppeDto);
    }

    private static FormidlingsgruppeMapper getInstance(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
        return "D".equals(ggArenaFormidlinggruppeDto.getOp_type())
                ? new DeleteFormidlingsgruppeMapper()
                : new InsertOgUpdateFormidlingsgruppeMapper();
    }

    protected abstract FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto);

    protected Operation mapOperation(String operation) {
        switch (operation) {
            case "I" : return Operation.INSERT;
            case "U" : return Operation.UPDATE;
            case "D" : return Operation.DELETE;
            default: throw new IllegalArgumentException("Ukjent op_type-verdi på Kafka: " + operation);
        }
    }

    protected LocalDateTime modDato(String mod_dato) {
        return ofNullable(mod_dato)
                .map(d -> LocalDateTime.parse(d, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(null);
    }
}
