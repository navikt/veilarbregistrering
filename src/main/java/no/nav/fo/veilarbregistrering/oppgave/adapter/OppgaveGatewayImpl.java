package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;

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

        OppgaveDto oppgaveDto = new OppgaveDto(oppgave.getAktorId().asString(),
                oppgave.getBeskrivelse(),
                OPPFOLGING,
                KONTAKT_BRUKER,
                oppgave.getFristFerdigstillelse().toString(),
                oppgave.getAktivDato().toString(),
                NORM,
                oppgave.getEnhetnr()
                        .map(Enhetnr::asString)
                        .orElse(null)
                );

        return restClient.opprettOppgave(oppgaveDto);
    }

}
