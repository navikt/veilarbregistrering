package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe;

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;

class DeleteFormidlingsgruppeMapper extends FormidlingsgruppeMapper {

    @Override
    protected FormidlingsgruppeEvent map(GgArenaFormidlinggruppeDto ggArenaFormidlinggruppeDto) {
        BeforeDto before = ggArenaFormidlinggruppeDto.getBefore();

        return new FormidlingsgruppeEvent(
                mapFoedselsnummer(before.getFODSELSNR()),
                before.getPERSON_ID(),
                before.getPERSON_ID_STATUS(),
                mapOperation(ggArenaFormidlinggruppeDto.getOp_type()),
                Formidlingsgruppe.of(before.getFORMIDLINGSGRUPPEKODE()),
                modDato(before.getMOD_DATO()),
                null,
                null);
    }
}