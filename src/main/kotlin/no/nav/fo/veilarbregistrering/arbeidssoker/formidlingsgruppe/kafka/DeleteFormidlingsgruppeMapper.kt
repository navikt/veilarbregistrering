package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.kafka

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent

internal class DeleteFormidlingsgruppeMapper : FormidlingsgruppeMapper() {
    override fun map(ggArenaFormidlinggruppeDto: GgArenaFormidlinggruppeDto): FormidlingsgruppeEndretEvent {
        val before = ggArenaFormidlinggruppeDto.before ?: throw InvalidFormidlingsgruppeEvent("Delete op requires non null before-element")
        return FormidlingsgruppeEndretEvent(
            mapFoedselsnummer(before.FODSELSNR),
            before.PERSON_ID,
            before.PERSON_ID_STATUS,
            mapOperation(ggArenaFormidlinggruppeDto.op_type),
            Formidlingsgruppe(before.FORMIDLINGSGRUPPEKODE),
            modDato(before.MOD_DATO),
            null,
            null
        )
    }
}