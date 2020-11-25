package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;

public class AktiverBrukerData {
    Fnr fnr;
    Innsatsgruppe innsatsgruppe;

    public AktiverBrukerData(Fnr fnr, Innsatsgruppe innsatsgruppe) {
        this.fnr = fnr;
        this.innsatsgruppe = innsatsgruppe;
    }

    public AktiverBrukerData() {
    }

    public Fnr getFnr() {
        return this.fnr;
    }

    public Innsatsgruppe getInnsatsgruppe() {
        return this.innsatsgruppe;
    }

    public void setFnr(Fnr fnr) {
        this.fnr = fnr;
    }

    public void setInnsatsgruppe(Innsatsgruppe innsatsgruppe) {
        this.innsatsgruppe = innsatsgruppe;
    }
}
