package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe.Companion.of
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent

internal class InsertFormidlingsgruppeMapper : FormidlingsgruppeMapper() {
    override fun map(ggArenaFormidlinggruppeDto: GgArenaFormidlinggruppeDto): FormidlingsgruppeEvent {
        val after = ggArenaFormidlinggruppeDto.after ?: throw InvalidFormidlingsgruppeEvent("Insert operation requires non null after-section")
        return FormidlingsgruppeEvent(
            mapFoedselsnummer(after.FODSELSNR),
            after.PERSON_ID,
            after.PERSON_ID_STATUS,
            mapOperation(ggArenaFormidlinggruppeDto.op_type),
            of(after.FORMIDLINGSGRUPPEKODE),
            modDato(after.MOD_DATO),
            null,
            null
        )
    }
}
