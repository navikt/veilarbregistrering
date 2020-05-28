package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;

public class OppgaveGatewayImpl implements OppgaveGateway {

    private static final String KONTAKT_BRUKER = "KONT_BRUK";
    private static final String OPPFOLGING = "OPP";
    private static final String NORM = "NORM";

    private final OppgaveRestClient restClient;

    public OppgaveGatewayImpl(OppgaveRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public OppgaveResponse opprett(Oppgave oppgave) {
        OppgaveDto oppgaveDto = new OppgaveDto();
        oppgaveDto.setAktoerId(oppgave.getAktorId().asString());
        oppgaveDto.setTildeltEnhetsnr(oppgave.getEnhetsnr()
                .map(Enhetsnr::asString)
                .orElse(null));
        oppgaveDto.setBeskrivelse(oppgave.getBeskrivelse());
        oppgaveDto.setTema(OPPFOLGING);
        oppgaveDto.setOppgavetype(KONTAKT_BRUKER);
        oppgaveDto.setFristFerdigstillelse(oppgave.getFristFerdigstilleles().toString());
        oppgaveDto.setAktivDato(oppgave.getAktivDato().toString());
        oppgaveDto.setPrioritet(NORM);

        return restClient.opprettOppgave(oppgaveDto);
    }

}
