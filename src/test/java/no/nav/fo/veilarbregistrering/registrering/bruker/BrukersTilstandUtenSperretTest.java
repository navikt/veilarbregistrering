package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe;
import no.nav.fo.veilarbregistrering.sykemelding.Maksdato;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.SYKMELDT_REGISTRERING;
import static org.assertj.core.api.Assertions.assertThat;

public class BrukersTilstandUtenSperretTest {

    @Test
    public void beregnRegistreringType_gir_SYKMELDT_REGISTRERING_når_bruker_er_sykemeldtMedArbeidsgiver_Og_Maksdato_Er_Null() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("VURDI"),
                Rettighetsgruppe.of("IYT"));

        SykmeldtInfoData sykeforlop = new SykmeldtInfoData(null, false);

        BrukersTilstand brukersTilstand = new BrukersTilstandUtenSperret(oppfolgingsstatus, sykeforlop);
        RegistreringType registreringType = brukersTilstand.getRegistreringstype();

        assertThat(registreringType).isEqualTo(SYKMELDT_REGISTRERING);
    }

    @Test
    public void beregnRegistreringType_gir_SYKMELDT_REGISTRERING_når_bruker_er_sykemeldtMedArbeidsgiver_Og_Maksdato_Er_Under_39_Uker() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("VURDI"),
                Rettighetsgruppe.of("IYT"));

        Maksdato maksdato = Maksdato.of("2021-03-01");

        SykmeldtInfoData sykeforlop = new SykmeldtInfoData(
                maksdato.asString(),
                maksdato.beregnSykmeldtMellom39Og52Uker(LocalDate.of(2020, 5, 1)));

        BrukersTilstand brukersTilstand = new BrukersTilstandUtenSperret(oppfolgingsstatus, sykeforlop);
        RegistreringType registreringType = brukersTilstand.getRegistreringstype();

        assertThat(registreringType).isEqualTo(SYKMELDT_REGISTRERING);
    }

    @Test
    public void beregnRegistreringType_gir_SYKMELDT_REGISTRERING_når_bruker_er_sykemeldtMedArbeidsgiver_Og_Maksdato_Er_Over_39_Uker() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("VURDI"),
                Rettighetsgruppe.of("IYT"));

        Maksdato maksdato = Maksdato.of("2020-06-01");

        SykmeldtInfoData sykeforlop = new SykmeldtInfoData(
                maksdato.asString(),
                maksdato.beregnSykmeldtMellom39Og52Uker(LocalDate.of(2020, 5, 1)));

        BrukersTilstand brukersTilstand = new BrukersTilstandUtenSperret(oppfolgingsstatus, sykeforlop);
        RegistreringType registreringType = brukersTilstand.getRegistreringstype();

        assertThat(registreringType).isEqualTo(SYKMELDT_REGISTRERING);
    }
}
