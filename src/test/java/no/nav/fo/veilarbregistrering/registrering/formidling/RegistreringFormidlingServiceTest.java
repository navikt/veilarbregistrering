package no.nav.fo.veilarbregistrering.registrering.formidling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RegistreringFormidlingServiceTest {

    private RegistreringFormidlingRepository registreringFormidlingRepository;

    private RegistreringFormidlingService registreringFormidlingService;

    @BeforeEach
    public void setup() {
        registreringFormidlingRepository = mock(RegistreringFormidlingRepository.class);

        registreringFormidlingService = new RegistreringFormidlingService(registreringFormidlingRepository);
    }

    @Test
    public void skal_oppdatere_registreringtilstand_med_status_og_sistendret() {
        LocalDateTime sistEndret = LocalDateTime.now();
        RegistreringFormidling original = RegistreringFormidlingTestdataBuilder
                .registreringTilstand()
                .status(Status.MOTTATT)
                .opprettet(sistEndret.minusDays(1))
                .sistEndret(sistEndret)
                .build();
        when(registreringFormidlingRepository.hentRegistreringTilstand(original.getId())).thenReturn(original);

        registreringFormidlingService.oppdaterRegistreringTilstand(OppdaterRegistreringFormidlingCommand.of(original.getId(), Status.OVERFORT_ARENA));

        ArgumentCaptor<RegistreringFormidling> argumentCaptor = ArgumentCaptor.forClass(RegistreringFormidling.class);
        verify(registreringFormidlingRepository).oppdater(argumentCaptor.capture());
        RegistreringFormidling capturedArgument = argumentCaptor.getValue();

        assertThat(capturedArgument.getId()).isEqualTo(original.getId());
        assertThat(capturedArgument.getBrukerRegistreringId()).isEqualTo(original.getBrukerRegistreringId());
        assertThat(capturedArgument.getOpprettet()).isEqualTo(original.getOpprettet());
        assertThat(capturedArgument.getStatus()).isEqualTo(Status.OVERFORT_ARENA);
    }
}
