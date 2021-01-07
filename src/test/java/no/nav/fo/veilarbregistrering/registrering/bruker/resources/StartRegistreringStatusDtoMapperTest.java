package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukersTilstand;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.ORDINAER_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.SYKMELDT_REGISTRERING;
import static org.assertj.core.api.Assertions.assertThat;

public class StartRegistreringStatusDtoMapperTest {

    @Test
    public void map_skal_håndtere_null_verdier() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                null,
                null,
                null,
                null,
                null);
        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData(null, false);
        BrukersTilstand brukersTilstand = new BrukersTilstand(oppfolgingsstatus, sykmeldtInfoData, false);

        StartRegistreringStatusDto dto = StartRegistreringStatusDtoMapper.map(
                brukersTilstand,
                Optional.empty(),
                false,
                0);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(dto.getRegistreringType()).isEqualTo(ORDINAER_REGISTRERING);
        softAssertions.assertThat(dto.getGeografiskTilknytning()).isNull();
        softAssertions.assertThat(dto.getJobbetSeksAvTolvSisteManeder()).isFalse();
        softAssertions.assertThat(dto.isErSykmeldtMedArbeidsgiver()).isFalse();
        softAssertions.assertThat(dto.isUnderOppfolging()).isFalse();
        softAssertions.assertThat(dto.getFormidlingsgruppe()).isNull();
        softAssertions.assertThat(dto.getMaksDato()).isNull();
        softAssertions.assertThat(dto.getRettighetsgruppe()).isNull();
        softAssertions.assertThat(dto.getServicegruppe()).isNull();

        softAssertions.assertAll();
    }

    @Test
    public void map_skal_håndtere_verdi_i_alle_felter() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("SERV"),
                Rettighetsgruppe.of("AAP"));
        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData("01122019", true);
        BrukersTilstand brukersTilstand = new BrukersTilstand(oppfolgingsstatus, sykmeldtInfoData, false);

        StartRegistreringStatusDto dto = StartRegistreringStatusDtoMapper.map(
                brukersTilstand,
                Optional.of(GeografiskTilknytning.of("030109")),
                true,
                30);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(dto.getRegistreringType()).isEqualTo(SYKMELDT_REGISTRERING);
        softAssertions.assertThat(dto.getGeografiskTilknytning()).isEqualTo("030109");
        softAssertions.assertThat(dto.getJobbetSeksAvTolvSisteManeder()).isTrue();
        softAssertions.assertThat(dto.isErSykmeldtMedArbeidsgiver()).isTrue();
        softAssertions.assertThat(dto.isUnderOppfolging()).isFalse();
        softAssertions.assertThat(dto.getFormidlingsgruppe()).isEqualTo("IARBS");
        softAssertions.assertThat(dto.getMaksDato()).isEqualTo("01122019");
        softAssertions.assertThat(dto.getRettighetsgruppe()).isEqualTo("AAP");
        softAssertions.assertThat(dto.getServicegruppe()).isEqualTo("SERV");

        softAssertions.assertAll();
    }

    @Test
    public void map_skal_mappe_erSykmeldtMedArbeidsgiver_Til() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                true,
                true,
                false,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("SERV"),
                Rettighetsgruppe.of("AAP"));
        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData("01122019", true);
        BrukersTilstand brukersTilstand = new BrukersTilstand(oppfolgingsstatus, sykmeldtInfoData, false);

        StartRegistreringStatusDto dto = StartRegistreringStatusDtoMapper.map(
                brukersTilstand,
                Optional.of(GeografiskTilknytning.of("030109")),
                false,
                30);

        assertThat(dto.isErSykmeldtMedArbeidsgiver()).isEqualTo(false);
    }
}
