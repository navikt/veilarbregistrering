package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.BesvarelseTestdataBuilder;

import java.time.LocalDateTime;

public class SykmeldtRegistreringTestdataBuilder {
    public static SykmeldtRegistrering gyldigSykmeldtRegistrering() {
        return new SykmeldtRegistrering()
                .setOpprettetDato(LocalDateTime.now())
                .setBesvarelse(BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse())
                .setTeksterForBesvarelse(TekstForSporsmalTestdataBuilder.gyldigeTeksterForSykmeldtBesvarelse());
    }
}
