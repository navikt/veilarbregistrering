package no.nav.fo.veilarbregistrering.oppfolging.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.ArenaStatusDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.KanReaktiveresDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.SammensattOppfolgingStatusException
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.VeilarbarenaClient
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

class OppfolgingGatewayImpl(private val oppfolgingClient: OppfolgingClient, private val veilarbarenaClient: VeilarbarenaClient) : OppfolgingGateway {
    override fun hentOppfolgingsstatus(fodselsnummer: Foedselsnummer): Oppfolgingsstatus {
        val oppfolgingStatusData = oppfolgingClient.hentOppfolgingsstatus(fodselsnummer)
        val oppfolgingsstatus = map(oppfolgingStatusData)

        val oppfolgingsstatusFraNyeKilder = hentOppfolgingsstatusFraNyeKilder(fodselsnummer)

        if (oppfolgingsstatus != oppfolgingsstatusFraNyeKilder) {
            logger.warn("Oppfolgingsstatus fra ny kilde er ulik eksisterende: Eksisterende: $oppfolgingsstatus Ny: $oppfolgingsstatusFraNyeKilder")
        }

        return oppfolgingsstatus
    }

    override fun hentOppfolgingsstatusFraNyeKilder(fodselsnummer: Foedselsnummer): Oppfolgingsstatus? {
        return try {
            val erUnderOppfolging: ErUnderOppfolgingDto = oppfolgingClient.erBrukerUnderOppfolging(fodselsnummer)
            val oppfolgingsstatus = veilarbarenaClient.arenaStatus(fodselsnummer)
            val kanReaktiveres = veilarbarenaClient.kanReaktiveres(fodselsnummer)
            map(erUnderOppfolging, oppfolgingsstatus, kanReaktiveres)
        } catch (e: SammensattOppfolgingStatusException) {
            logger.warn("Feil ved henting av oppfolgingsstatus fra nye kilder", e)
            null
        }
    }

    override fun aktiverBruker(foedselsnummer: Foedselsnummer, innsatsgruppe: Innsatsgruppe) {
        oppfolgingClient.aktiverBruker(AktiverBrukerData(Fnr(foedselsnummer.stringValue()), innsatsgruppe))
    }

    override fun reaktiverBruker(fodselsnummer: Foedselsnummer) {
        oppfolgingClient.reaktiverBruker(Fnr(fodselsnummer.stringValue()))
    }

    override fun aktiverSykmeldt(fodselsnummer: Foedselsnummer, besvarelse: Besvarelse) {
        oppfolgingClient.aktiverSykmeldt(SykmeldtBrukerType.of(besvarelse), fodselsnummer)
    }

    companion object {
        private fun map(erUnderOppfolgingDto: ErUnderOppfolgingDto, arenastatus: ArenaStatusDto, kanReaktiveresDto: KanReaktiveresDto): Oppfolgingsstatus {
            return Oppfolgingsstatus(
                    isUnderOppfolging = erUnderOppfolgingDto.erUnderOppfolging,
                    kanReaktiveres = kanReaktiveresDto.kanEnkeltReaktiveres,
                    erSykmeldtMedArbeidsgiver = arenastatus.formidlingsgruppe == "IARBS" && erUnderOppfolgingDto.erUnderOppfolging,
                    formidlingsgruppe = Formidlingsgruppe.of(arenastatus.formidlingsgruppe),
                    servicegruppe = Servicegruppe.of(arenastatus.kvalifiseringsgruppe),
                    rettighetsgruppe = Rettighetsgruppe.of(arenastatus.rettighetsgruppe)
            )
        }

        private fun map(oppfolgingStatusData: OppfolgingStatusData): Oppfolgingsstatus {
            return Oppfolgingsstatus(
                    oppfolgingStatusData.underOppfolging,
                    oppfolgingStatusData.kanReaktiveres,
                    oppfolgingStatusData.erSykmeldtMedArbeidsgiver,
                    oppfolgingStatusData.formidlingsgruppe?.let(Formidlingsgruppe::of),
                    oppfolgingStatusData.servicegruppe?.let(::Servicegruppe),
                    oppfolgingStatusData.rettighetsgruppe?.let(Rettighetsgruppe::of)
            )
        }
    }
}