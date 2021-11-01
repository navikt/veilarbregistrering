package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.module.kotlin.ExtensionsKt.jacksonObjectMapper;
import static java.util.Optional.ofNullable;

public abstract class FormidlingsgruppeMapper {

    private static final ObjectMapper json = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static FormidlingsgruppeEvent map(String record) throws RuntimeException {
        GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto;
        try {
            ggArenaFormidlinggruppeDto = json.readValue(record, GgArenaFormidlinggruppeDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Factory.getInstance(ggArenaFormidlinggruppeDto).map(ggArenaFormidlinggruppeDto);
    }

    protected abstract FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto);

    protected Foedselsnummer mapFoedselsnummer(String fodselsnr) {
        return ofNullable(fodselsnr)
                .map(Foedselsnummer::of)
                .orElse(null);
    }

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

    static class Factory {

        private final static DeleteFormidlingsgruppeMapper deleteFormidlingsgruppeMapper = new DeleteFormidlingsgruppeMapper();
        private final static UpdateFormidlingsgruppeMapper updateFormidlingsgruppeMapper = new UpdateFormidlingsgruppeMapper();
        private final static InsertFormidlingsgruppeMapper insertFormidlingsgruppeMapper = new InsertFormidlingsgruppeMapper();

        private static FormidlingsgruppeMapper getInstance(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
            if ("D".equals(ggArenaFormidlinggruppeDto.getOp_type())) {
                return deleteFormidlingsgruppeMapper;
            } else if ("U".equals(ggArenaFormidlinggruppeDto.getOp_type())) {
                return updateFormidlingsgruppeMapper;
            } else if ("I".equals(ggArenaFormidlinggruppeDto.getOp_type())) {
                return insertFormidlingsgruppeMapper;
            }
            throw new IllegalArgumentException("Ukjent op_type fra Arena: " + ggArenaFormidlinggruppeDto.getOp_type());
        }
    }
}
