package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent

internal class DeleteFormidlingsgruppeMapper : FormidlingsgruppeMapper() {
    override fun map(ggArenaFormidlinggruppeDto: GgArenaFormidlinggruppeDto): FormidlingsgruppeEvent {
        val before = ggArenaFormidlinggruppeDto.before ?: throw InvalidFormidlingsgruppeEvent("Delete op requires non null before-element")
        return FormidlingsgruppeEvent(
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