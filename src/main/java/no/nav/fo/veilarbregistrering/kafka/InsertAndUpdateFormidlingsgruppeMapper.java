package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import static java.util.Optional.ofNullable;

class InsertAndUpdateFormidlingsgruppeMapper extends FormidlingsgruppeMapper {

    @Override
    protected FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
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
}