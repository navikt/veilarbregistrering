package no.nav.fo.veilarbregistrering.oppfolging.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

class OppfolgingGatewayImpl(private val oppfolgingClient: OppfolgingClient) : OppfolgingGateway {
    override fun hentOppfolgingsstatus(fodselsnummer: Foedselsnummer): Oppfolgingsstatus {
        val oppfolgingStatusData = oppfolgingClient.hentOppfolgingsstatus(fodselsnummer)
        return map(oppfolgingStatusData)
    }

    override fun aktiverBruker(foedselsnummer: Foedselsnummer, innsatsgruppe: Innsatsgruppe) {
        oppfolgingClient.aktiverBruker(AktiverBrukerData(Fnr(foedselsnummer.stringValue()), innsatsgruppe))
    }

    override fun reaktiverBruker(fodselsnummer: Foedselsnummer) {
        oppfolgingClient.reaktiverBruker(Fnr(fodselsnummer.stringValue()))
    }

    override fun settOppfolgingSykmeldt(fodselsnummer: Foedselsnummer, besvarelse: Besvarelse) {
        oppfolgingClient.settOppfolgingSykmeldt(SykmeldtBrukerType.of(besvarelse), fodselsnummer)
    }

    companion object {
        private fun map(oppfolgingStatusData: OppfolgingStatusData): Oppfolgingsstatus {
            return Oppfolgingsstatus(
                    oppfolgingStatusData.isUnderOppfolging,
                    oppfolgingStatusData.getKanReaktiveres(),
                    oppfolgingStatusData.getErSykmeldtMedArbeidsgiver(),
                    if (oppfolgingStatusData.getFormidlingsgruppe() != null) Formidlingsgruppe.of(oppfolgingStatusData.getFormidlingsgruppe()) else null,
                    if (oppfolgingStatusData.getServicegruppe() != null) Servicegruppe.of(oppfolgingStatusData.getServicegruppe()) else null,
                    if (oppfolgingStatusData.getRettighetsgruppe() != null) Rettighetsgruppe.of(oppfolgingStatusData.getRettighetsgruppe()) else null)
        }
    }
}