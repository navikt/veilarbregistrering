package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.util.ArrayList;
import java.util.List;

public class TekstForSporsmalTestdataBuilder {
    public static List<TekstForSporsmal> gyldigeTeksterForBesvarelse() {
        List<TekstForSporsmal> tekster = new ArrayList<>();
        tekster.add(new TekstForSporsmal("utdanning", "Hva er din høyeste fullførte utdanning?", "Høyere utdanning (5 år eller mer)"));
        tekster.add(new TekstForSporsmal("utdanningBestatt", "Er utdanningen din bestått?", "Ja"));
        tekster.add(new TekstForSporsmal("utdanningGodkjent", "Er utdanningen din godkjent i Norge?", "Nei"));
        tekster.add(new TekstForSporsmal("helseHinder", "Trenger du oppfølging i forbindelse med helseutfordringer?", "Nei"));
        tekster.add(new TekstForSporsmal("andreForhold", "Trenger du oppfølging i forbindelse med andre utfordringer?", "Nei"));
        tekster.add(new TekstForSporsmal("sisteStilling", "Din siste jobb", "Har hatt jobb"));
        tekster.add(new TekstForSporsmal("dinSituasjon", "Hvorfor registrerer du deg?", "Jeg er permittert eller vil bli permittert"));
        return tekster;
    }

    public static List<TekstForSporsmal> gyldigeTeksterForSykmeldtBesvarelse() {
        List<TekstForSporsmal> tekster = new ArrayList<>();
        tekster.add(new TekstForSporsmal("fremtidigSituasjon", "Hva tenker du om din fremtidige situasjon?", "Jeg skal tilbake til jobben jeg har"));
        tekster.add(new TekstForSporsmal("tilbakeIArbeid", "Tror du at du kommer tilbake i jobb før du har vært sykmeldt i 52 uker?", "Nei"));
        return tekster;
    }
}
