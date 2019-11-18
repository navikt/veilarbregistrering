package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;

import java.time.LocalDate;

public class OppgaveGatewayImpl implements OppgaveGateway {

    private final OppgaveRestClient restClient;

    public OppgaveGatewayImpl(OppgaveRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Oppgave opprettOppgave(String aktoerId) {
        OppgaveDto oppgaveDto = new OppgaveDto();
        oppgaveDto.setAktoerId(aktoerId);
        oppgaveDto.setBeskrivelse(
                "Denne oppgaven har bruker selv opprettet, og er en pilotering på NAV Grünerløkka." +
                " Brukeren får ikke registrert seg som arbeidssøker." +
                " Kontaktperson ved NAV Grünerløkka er Avdullah Demiri.");
        oppgaveDto.setTilordnetRessurs("D113328"); // Avdullah Demiri
        oppgaveDto.setTema("OPP");
        oppgaveDto.setOppgavetype("KONT_BRUK");
        oppgaveDto.setFristFerdigstillelse(LocalDate.now().plusDays(2).toString());
        oppgaveDto.setAktivDato(LocalDate.now().toString());
        oppgaveDto.setPrioritet("LAV");

        return restClient.opprettOppgave(oppgaveDto);
    }
}
