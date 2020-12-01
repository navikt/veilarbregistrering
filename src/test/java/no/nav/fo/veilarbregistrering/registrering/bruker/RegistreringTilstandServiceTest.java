package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringTilstandDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RegistreringTilstandServiceTest {

    private RegistreringTilstandRepository registreringTilstandRepository;

    private RegistreringTilstandService registreringTilstandService;

    @BeforeEach
    public void setup() {
        registreringTilstandRepository = mock(RegistreringTilstandRepository.class);

        registreringTilstandService = new RegistreringTilstandService(registreringTilstandRepository);
    }

    @Test
    public void skal_oppdatere_registreringtilstand_med_status_og_sistendret() {
        LocalDateTime sistEndret = LocalDateTime.now();
        RegistreringTilstand original = RegistreringTilstandTestdataBuilder
                .registreringTilstand()
                .status(Status.ARENA_OK)
                .opprettet(sistEndret.minusDays(1))
                .sistEndret(sistEndret)
                .build();
        when(registreringTilstandRepository.hentRegistreringTilstand(original.getId())).thenReturn(original);

        registreringTilstandService.oppdaterRegistreringTilstand(RegistreringTilstandDto.of(original.getId(), Status.MANGLER_ARBEIDSTILLATELSE));

        ArgumentCaptor<RegistreringTilstand> argumentCaptor = ArgumentCaptor.forClass(RegistreringTilstand.class);
        verify(registreringTilstandRepository).oppdater(argumentCaptor.capture());
        RegistreringTilstand capturedArgument = argumentCaptor.getValue();

        assertThat(capturedArgument.getId()).isEqualTo(original.getId());
        assertThat(capturedArgument.getBrukerRegistreringId()).isEqualTo(original.getBrukerRegistreringId());
        assertThat(capturedArgument.getOpprettet()).isEqualTo(original.getOpprettet());
        assertThat(capturedArgument.getStatus()).isEqualTo(Status.MANGLER_ARBEIDSTILLATELSE);
    }
}
