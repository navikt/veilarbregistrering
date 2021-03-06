package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe;

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;

class UpdateFormidlingsgruppeMapper extends FormidlingsgruppeMapper {

    @Override
    protected FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
        AfterDto after = ggArenaFormidlinggruppeDto.getAfter();
        BeforeDto before = ggArenaFormidlinggruppeDto.getBefore();

        return new FormidlingsgruppeEvent(
                mapFoedselsnummer(after.getFODSELSNR()),
                after.getPERSON_ID(),
                after.getPERSON_ID_STATUS(),
                mapOperation(ggArenaFormidlinggruppeDto.getOp_type()),
                Formidlingsgruppe.of(after.getFORMIDLINGSGRUPPEKODE()),
                modDato(after.getMOD_DATO()),
                before != null ? Formidlingsgruppe.of(before.getFORMIDLINGSGRUPPEKODE()) : null,
                before != null ? modDato(before.getMOD_DATO()) : null);
    }
}