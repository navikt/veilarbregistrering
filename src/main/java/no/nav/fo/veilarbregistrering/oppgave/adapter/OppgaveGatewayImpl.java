package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;

public class OppgaveGatewayImpl implements OppgaveGateway {

    private static final String OPPGAVETYPE_KONTAKT_BRUKER = "KONT_BRUK";
    private static final String TEMA_OPPFOLGING = "OPP";
    private static final String PRIORITET_NORM = "NORM";

    private final OppgaveRestClient restClient;

    public OppgaveGatewayImpl(OppgaveRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public OppgaveResponse opprett(Oppgave oppgave) {

        OppgaveDto oppgaveDto = new OppgaveDto(oppgave.getAktorId().asString(),
                oppgave.getBeskrivelse(),
                TEMA_OPPFOLGING,
                OPPGAVETYPE_KONTAKT_BRUKER,
                oppgave.getFristFerdigstillelse().toString(),
                oppgave.getAktivDato().toString(),
                PRIORITET_NORM,
                oppgave.getEnhetnr()
                        .map(Enhetnr::asString)
                        .orElse(null)
                );

        return restClient.opprettOppgave(oppgaveDto);
    }

}
