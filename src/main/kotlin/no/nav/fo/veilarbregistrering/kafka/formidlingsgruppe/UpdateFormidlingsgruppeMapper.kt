package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEvent

internal class UpdateFormidlingsgruppeMapper : FormidlingsgruppeMapper() {
    override fun map(ggArenaFormidlinggruppeDto: GgArenaFormidlinggruppeDto): FormidlingsgruppeEvent {
        val after = ggArenaFormidlinggruppeDto.after ?: throw InvalidFormidlingsgruppeEvent("Update op requires non null after-element")
        val before = ggArenaFormidlinggruppeDto.before ?: throw InvalidFormidlingsgruppeEvent("Update op requires non null before-element")
        return FormidlingsgruppeEvent(
            mapFoedselsnummer(after.FODSELSNR),
            after.PERSON_ID,
            after.PERSON_ID_STATUS,
            mapOperation(ggArenaFormidlinggruppeDto.op_type),
            Formidlingsgruppe(after.FORMIDLINGSGRUPPEKODE),
            modDato(after.MOD_DATO),
            Formidlingsgruppe(before.FORMIDLINGSGRUPPEKODE),
            modDato(before.MOD_DATO)
        )
    }
}