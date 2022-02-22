package no.nav.fo.veilarbregistrering.oppfolging.adapter

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events.OPPFOLGINSSTATUS_MANGLER_DATA_NY_KILDE
import no.nav.fo.veilarbregistrering.metrics.JaNei
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.ArenaStatusDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.KanReaktiveresDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.SammensattOppfolgingStatusException
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.VeilarbarenaClient
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

class OppfolgingGatewayImpl(
    private val oppfolgingClient: OppfolgingClient,
    private val veilarbarenaClient: VeilarbarenaClient,
    private val unleashClient: UnleashClient,
    private val metricsService: PrometheusMetricsService
) : OppfolgingGateway {
    override fun hentOppfolgingsstatus(fodselsnummer: Foedselsnummer): Oppfolgingsstatus {
        val oppfolgingStatusData = oppfolgingClient.hentOppfolgingsstatus(fodselsnummer)
        val oppfolgingsstatus = map(oppfolgingStatusData)

        if (unleashClient.isEnabled("veilarbregistrering.hentOppfolgingsstatusFraNyeKilder")) {

            val oppfolgingsstatusFraNyeKilder = try {
                val erUnderOppfolging: ErUnderOppfolgingDto = oppfolgingClient.erBrukerUnderOppfolging(fodselsnummer)
                val arenastatus = veilarbarenaClient.arenaStatus(fodselsnummer)
                val kanReaktiveres = veilarbarenaClient.kanReaktiveres(fodselsnummer)
                val oppfolgingsstatusNy = map(erUnderOppfolging, arenastatus, kanReaktiveres)

                if (oppfolgingsstatus.manglerArenstatusFraNyKilde(oppfolgingsstatusNy)) {
                    metricsService.registrer(OPPFOLGINSSTATUS_MANGLER_DATA_NY_KILDE, JaNei.JA)
                } else {
                    metricsService.registrer(OPPFOLGINSSTATUS_MANGLER_DATA_NY_KILDE, JaNei.NEI)
                }
                oppfolgingsstatusNy
            } catch (e: SammensattOppfolgingStatusException) {
                logger.warn("Feil ved henting av oppfolgingsstatus fra nye kilder", e)
                null
            }

            if (isDevelopment() && oppfolgingsstatusFraNyeKilder != null) {
                return oppfolgingsstatusFraNyeKilder
            }
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
        private fun map(
            erUnderOppfolgingDto: ErUnderOppfolgingDto,
            arenastatus: ArenaStatusDto?,
            kanReaktiveresDto: KanReaktiveresDto
        ): Oppfolgingsstatus {
            return if (arenastatus != null) {
                Oppfolgingsstatus(
                    isUnderOppfolging = erUnderOppfolgingDto.erUnderOppfolging,
                    kanReaktiveres = kanReaktiveresDto.kanEnkeltReaktiveres,
                    erSykmeldtMedArbeidsgiver = arenastatus.erSykmeldtMedArbeidsgiver(),
                    formidlingsgruppe = Formidlingsgruppe(arenastatus.formidlingsgruppe),
                    servicegruppe = Servicegruppe(arenastatus.kvalifiseringsgruppe),
                    rettighetsgruppe = Rettighetsgruppe(arenastatus.rettighetsgruppe)
                )
            } else {
                Oppfolgingsstatus(
                    isUnderOppfolging = erUnderOppfolgingDto.erUnderOppfolging,
                    kanReaktiveres = kanReaktiveresDto.kanEnkeltReaktiveres
                )
            }
        }

        private fun map(oppfolgingStatusData: OppfolgingStatusData): Oppfolgingsstatus {
            return Oppfolgingsstatus(
                isUnderOppfolging = oppfolgingStatusData.underOppfolging,
                kanReaktiveres = oppfolgingStatusData.kanReaktiveres,
                erSykmeldtMedArbeidsgiver = oppfolgingStatusData.erSykmeldtMedArbeidsgiver,
                formidlingsgruppe = oppfolgingStatusData.formidlingsgruppe?.let(::Formidlingsgruppe),
                servicegruppe = oppfolgingStatusData.servicegruppe?.let(::Servicegruppe),
                rettighetsgruppe = oppfolgingStatusData.rettighetsgruppe?.let(::Rettighetsgruppe)
            )
        }
    }
}

fun ArenaStatusDto.erSykmeldtMedArbeidsgiver(): Boolean =
    formidlingsgruppe == "IARBS" && !setOf(
        "IKVAL",
        "BFORM",
        "BATT",
        "VARIG",
        "VURDU",
        "OPPFI",
    ).contains(kvalifiseringsgruppe)