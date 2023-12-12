package no.nav.paw.arbeidssokerregisteret.intern.v1

import java.util.*

const val startetHendelseType = "intern.v1.startet"
const val avsluttetHendelseType = "intern.v1.avsluttet"
const val opplysningerOmArbeidssoekerHendelseType = "intern.v1.opplysninger_om_arbeidssoeker"

interface Hendelse : HarIdentitetsnummer, HarMetadata {
    val hendelseId: UUID
    val hendelseType: String
}
