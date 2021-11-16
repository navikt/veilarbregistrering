package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.metrics.Metric

@Suppress("EnumEntryName")
enum class RoutingStep : Metric {
    GeografiskTilknytning_Feilet,
    GeografiskTilknytning_Funnet,
    GeografiskTilknytning_ByMedBydel_Funnet,
    GeografiskTilknytning_Utland,
    Enhetsnummer_Feilet,
    SisteArbeidsforhold_IkkeFunnet,
    OrgNummer_ikkeFunnet,
    OrgDetaljer_IkkeFunnet,
    Kommunenummer_IkkeFunnet,
    Enhetsnummer_IkkeFunnet,
    Enhetsnummer_Funnet;

    override fun fieldName(): String {
        return "routingStep"
    }

    override fun value(): Any {
        return this.toString()
    }
}