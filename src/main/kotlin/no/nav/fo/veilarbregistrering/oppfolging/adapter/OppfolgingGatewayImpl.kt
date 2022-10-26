package no.nav.fo.veilarbregistrering.oppfolging.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.oppfolging.*
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.ArenaStatusDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.KanReaktiveresDto
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.VeilarbarenaClient
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

class OppfolgingGatewayImpl(
    private val oppfolgingClient: OppfolgingClient,
    private val veilarbarenaClient: VeilarbarenaClient
) : OppfolgingGateway {
    override fun hentOppfolgingsstatus(fodselsnummer: Foedselsnummer): Oppfolgingsstatus {
        val erUnderOppfolging: ErUnderOppfolgingDto = oppfolgingClient.erBrukerUnderOppfolging(fodselsnummer)
        val arenastatus = veilarbarenaClient.arenaStatus(fodselsnummer)
        val kanReaktiveres = veilarbarenaClient.kanReaktiveres(fodselsnummer)
        return map(erUnderOppfolging, arenastatus, kanReaktiveres)
    }

    override fun arenaStatus(fodselsnummer: Foedselsnummer): ArenaStatus? {
        val arenaStatus = veilarbarenaClient.arenaStatus(fodselsnummer) ?: return null
        return ArenaStatus(
            Servicegruppe(arenaStatus.kvalifiseringsgruppe),
            Rettighetsgruppe(arenaStatus.rettighetsgruppe),
            Formidlingsgruppe(arenaStatus.formidlingsgruppe))
    }

    override fun erUnderOppfolging(fodselsnummer: Foedselsnummer): Boolean {
        return oppfolgingClient.erBrukerUnderOppfolging(fodselsnummer).erUnderOppfolging
    }

    override fun kanReaktiveres(fodselsnummer: Foedselsnummer): Boolean? {
        return veilarbarenaClient.kanReaktiveres(fodselsnummer).kanEnkeltReaktiveres
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