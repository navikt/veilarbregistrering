package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringTilstandDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AktiveringTilstandServiceTest {

    private AktiveringTilstandRepository aktiveringTilstandRepository;

    private AktiveringTilstandService aktiveringTilstandService;

    @BeforeEach
    public void setup() {
        aktiveringTilstandRepository = mock(AktiveringTilstandRepository.class);

        aktiveringTilstandService = new AktiveringTilstandService(aktiveringTilstandRepository);
    }

    @Test
    public void skal_oppdatere_registreringtilstand_med_status_og_sistendret() {
        LocalDateTime sistEndret = LocalDateTime.now();
        AktiveringTilstand original = RegistreringTilstandTestdataBuilder
                .registreringTilstand()
                .status(Status.ARENA_OK)
                .opprettet(sistEndret.minusDays(1))
                .sistEndret(sistEndret)
                .build();
        when(aktiveringTilstandRepository.hentAktiveringTilstand(original.getId())).thenReturn(original);

        aktiveringTilstandService.oppdaterRegistreringTilstand(RegistreringTilstandDto.of(original.getId(), Status.MANGLER_ARBEIDSTILLATELSE));

        ArgumentCaptor<AktiveringTilstand> argumentCaptor = ArgumentCaptor.forClass(AktiveringTilstand.class);
        verify(aktiveringTilstandRepository).oppdater(argumentCaptor.capture());
        AktiveringTilstand capturedArgument = argumentCaptor.getValue();

        assertThat(capturedArgument.getId()).isEqualTo(original.getId());
        assertThat(capturedArgument.getUuid()).isEqualTo(original.getUuid());
        assertThat(capturedArgument.getBrukerRegistreringId()).isEqualTo(original.getBrukerRegistreringId());
        assertThat(capturedArgument.getOpprettet()).isEqualTo(original.getOpprettet());
        assertThat(capturedArgument.getStatus()).isEqualTo(Status.MANGLER_ARBEIDSTILLATELSE);
    }
}
