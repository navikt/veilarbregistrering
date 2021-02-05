package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.mockito.Mockito.*;

public class ArbeidssokerServiceBehandleTest {

    private ArbeidssokerService arbeidssokerService;
    private ArbeidssokerRepository arbeidssokerRepository;

    @BeforeEach
    public void setup() {
        arbeidssokerRepository = mock(ArbeidssokerRepository.class);

        arbeidssokerService = new ArbeidssokerService(
                arbeidssokerRepository,
                mock(FormidlingsgruppeGateway.class),
                mock(UnleashService.class),
                mock(MetricsService.class)
        );
    }

    @Test
    public void endringer_fra_2010_skal_persisteres() {

        FormidlingsgruppeEvent formidlingsgruppeEvent = testEvent(LocalDateTime.of(2010, Month.JANUARY, 01, 00,00,00));

        arbeidssokerService.behandle(formidlingsgruppeEvent);

        verify(arbeidssokerRepository, times(1)).lagre(formidlingsgruppeEvent);
    }

    @Test
    public void endringer_foer_2010_skal_ikke_persisteres() {

        FormidlingsgruppeEvent formidlingsgruppeEvent = testEvent(LocalDateTime.of(2009, Month.DECEMBER, 31, 23,59,59));

        arbeidssokerService.behandle(formidlingsgruppeEvent);

        verify(arbeidssokerRepository, never()).lagre(formidlingsgruppeEvent);
    }

    private FormidlingsgruppeEvent testEvent(LocalDateTime test) {
        return new FormidlingsgruppeEvent(
                Foedselsnummer.of("12345678910"),
                "012345",
                "AKTIV",
                Operation.UPDATE,
                Formidlingsgruppe.of("ISERV"),
                test,
                Formidlingsgruppe.of("ARBS"),
                test.minusDays(1)
        );
    }
}
