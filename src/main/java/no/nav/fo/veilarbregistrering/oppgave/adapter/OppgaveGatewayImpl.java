package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;

import java.time.LocalDate;

public class OppgaveGatewayImpl implements OppgaveGateway {

    private static final String KONTAKT_BRUKER = "KONT_BRUK";
    private static final String OPPFOLGING = "OPP";
    private static final String NORM = "NORM";

    private final OppgaveRestClient restClient;

    public OppgaveGatewayImpl(OppgaveRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Oppgave opprettOppgave(AktorId aktoerId, Enhetsnr enhetsnr, String beskrivelse) {
        OppgaveDto oppgaveDto = new OppgaveDto();
        oppgaveDto.setAktoerId(aktoerId.asString());
        oppgaveDto.setTildeltEnhetsnr(enhetsnr != null ? enhetsnr.asString() : null);
        oppgaveDto.setBeskrivelse(beskrivelse);
        oppgaveDto.setTema(OPPFOLGING);
        oppgaveDto.setOppgavetype(KONTAKT_BRUKER);
        oppgaveDto.setFristFerdigstillelse(LocalDate.now().plusDays(2).toString());
        oppgaveDto.setAktivDato(LocalDate.now().toString());
        oppgaveDto.setPrioritet(NORM);

        return restClient.opprettOppgave(oppgaveDto);
    }

}
