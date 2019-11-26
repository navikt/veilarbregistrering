package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder;
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.besvarelse.SisteStillingSvar;
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder;

import java.time.LocalDateTime;

public class OrdinaerBrukerRegistreringTestdataBuilder {

    public static OrdinaerBrukerRegistrering gyldigBrukerRegistrering() {
        return new OrdinaerBrukerRegistrering()
                .setOpprettetDato(LocalDateTime.now())
                .setSisteStilling(StillingTestdataBuilder.gyldigStilling())
                .setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse())
                .setTeksterForBesvarelse(TekstForSporsmalTestdataBuilder.gyldigeTeksterForBesvarelse());
    }

    public static OrdinaerBrukerRegistrering gyldigBrukerRegistreringUtenJobb() {
        return gyldigBrukerRegistrering().setSisteStilling(
                StillingTestdataBuilder.ingenYrkesbakgrunn()
        ).setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.ALDRI_HATT_JOBB)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
    }
}
