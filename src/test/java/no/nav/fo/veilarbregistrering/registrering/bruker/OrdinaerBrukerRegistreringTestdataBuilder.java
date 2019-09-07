package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.BesvarelseTestdataBuilder;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.SisteStillingSvar;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.StillingTestdataBuilder;

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
