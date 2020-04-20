package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import org.junit.Before;
import org.junit.Test;

import static no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe.STANDARD_INNSATS;
import static no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ArenaOverforingServiceTest {

    private static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("12345678911");
    private static final long BRUKER_REGISTRERING_ID = 2334L;

    private ArenaOverforingService arenaOverforingService;
    private ProfileringRepository profileringRepository;
    private OppfolgingGateway oppfolgingClient;

    @Before
    public void setUp() {
        profileringRepository = mock(ProfileringRepository.class);
        BrukerRegistreringRepository brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        oppfolgingClient = mock(OppfolgingGateway.class);
        // uten innhold
        ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer = (aktorId, brukersSituasjon, opprettetDato) -> {
            // uten innhold
        };
        arenaOverforingService = new ArenaOverforingService(
                profileringRepository, brukerRegistreringRepository, oppfolgingClient, arbeidssokerRegistrertProducer);
    }

    @Test
    public void gitt_at_overfoer_gikk_bra_skal_status_vaere_ARENA_OK() {
        when(profileringRepository.hentProfileringForId(BRUKER_REGISTRERING_ID)).thenReturn(lagProfilering());

        Status status = arenaOverforingService.overfoerRegistreringTilArena(FOEDSELSNUMMER, STANDARD_INNSATS);

        verify(oppfolgingClient, times(1)).aktiverBruker(FOEDSELSNUMMER, STANDARD_INNSATS);
        assertThat(status).isEqualTo(Status.ARENA_OK);
    }

    @Test
    public void gitt_at_overfoer_feiler_teknisk_skal_status_vaere_TEKNISK_FEIL() {
        when(profileringRepository.hentProfileringForId(BRUKER_REGISTRERING_ID)).thenReturn(lagProfilering());
        doThrow(new RuntimeException("Noe teknisk feilet"))
                .when(oppfolgingClient).aktiverBruker(FOEDSELSNUMMER, STANDARD_INNSATS);

        Status status = arenaOverforingService.overfoerRegistreringTilArena(FOEDSELSNUMMER, STANDARD_INNSATS);

        assertThat(status).isEqualTo(Status.TEKNISK_FEIL);
    }
    
}
