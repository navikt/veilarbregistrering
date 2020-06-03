package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public enum  RoutingStep implements Metric {

    GeografiskTilknytning_Feilet,
    GeografiskTilknytning_Funnet,
    Enhetsnummer_Feilet,
    SisteArbeidsforhold_IkkeFunnet,
    OrgNummer_ikkeFunnet,
    OrgDetaljer_IkkeFunnet,
    Kommunenummer_IkkeFunnet,
    Enhetsnummer_IkkeFunnet,
    Enhetsnummer_Funnet;

    @Override
    public String fieldName() {
        return "routingStep";
    }

    @Override
    public Object value() {
        return this.toString();
    }
}
