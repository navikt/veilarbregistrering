package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe;

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;

import static java.util.Optional.ofNullable;

class DeleteFormidlingsgruppeMapper extends FormidlingsgruppeMapper {

    @Override
    protected FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
        BeforeDto before = ggArenaFormidlinggruppeDto.getBefore();

        Foedselsnummer foedselsnummer = ofNullable(before.getFODSELSNR())
                .map(Foedselsnummer::of)
                .orElse(null);

        return new FormidlingsgruppeEvent(
                foedselsnummer,
                before.getPERSON_ID(),
                before.getPERSON_ID_STATUS(),
                mapOperation(ggArenaFormidlinggruppeDto.getOp_type()),
                Formidlingsgruppe.of(before.getFORMIDLINGSGRUPPEKODE()),
                modDato(before.getMOD_DATO()),
                null,
                null);
    }
}