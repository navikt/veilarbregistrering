package no.nav.fo.veilarbregistrering.kafka;

import com.google.gson.Gson;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Optional.ofNullable;

class FormidlingsgruppeMapper {

    private static final Gson GSON = new Gson();

    static FormidlingsgruppeEvent map(String record) {
        GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto = GSON.fromJson(record, GgArenaFormidlinggruppeDto.class);
        return map(ggArenaFormidlinggruppeDto);
    }

    private static FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
        AfterDto after = ggArenaFormidlinggruppeDto.getAfter();

        Foedselsnummer foedselsnummer = ofNullable(after.getFODSELSNR())
                .map(Foedselsnummer::of)
                .orElse(null);

        BeforeDto before = ggArenaFormidlinggruppeDto.getBefore();

        return new FormidlingsgruppeEvent(
                foedselsnummer,
                after.getPERSON_ID(),
                after.getPERSON_ID_STATUS(),
                mapOperation(ggArenaFormidlinggruppeDto.getOp_type()),
                Formidlingsgruppe.of(after.getFORMIDLINGSGRUPPEKODE()),
                modDato(after.getMOD_DATO()),
                before != null ? Formidlingsgruppe.of(before.getFORMIDLINGSGRUPPEKODE()) : null,
                before != null ? modDato(before.getMOD_DATO()) : null);
    }

    private static Operation mapOperation(String operation) {
        switch (operation) {
            case "I" : return Operation.INSERT;
            case "U" : return Operation.UPDATE;
            case "D" : return Operation.DELETE;
            default: throw new IllegalArgumentException("Ukjent op_type-verdi på Kafka: " + operation);
        }
    }

    private static LocalDateTime modDato(String mod_dato) {
        return ofNullable(mod_dato)
                .map(d -> LocalDateTime.parse(d, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(null);
    }

}
