package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssoker
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.ArbeidssokerperioderMapper.filtrerBortIkkeAktivPersonIdOgInitielleISERVInserts
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository

/**
 * Har som oppgave å hente rådata fra formidlingsgruppe, ordinær registrering og reaktivering, sortere dataene med den
 * eldste først, og anvende de for å populere ny modell for Arbeidssøker.
 *
 * Hensikten videre er å kunne anvende denne i flere contexter - ifm. henting av perioder, starting av perioder og
 * avslutting av perioder. Alle ulike tilfeller hvor vi i sanntid skal populere periodene før vi har populert de for alle.
 *
 * På sikt bør det ikke være nødvendig å hente alle rådataene, men bare populere tilstanden på bakgrunn av egen databasetabell.
 */
class PopulerArbeidssokerperioderService(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val brukerReaktiveringRepository: ReaktiveringRepository
) {
    /**
     * Hent arbeidssøker er ment å abstrahere bort detaljene for hvordan en arbeidssøker fremstilles. Om det er
     * populering on-the-fly, eller om det er henting av allere populert og persisterte perioder i databasen.
     */
    fun hentArbeidssøker(bruker: Bruker): Arbeidssoker {
        return populerArbeidssøker(bruker.gjeldendeFoedselsnummer, bruker.historiskeFoedselsnummer)
    }

    fun hentArbeidssøker(foedselsnummer: Foedselsnummer): Arbeidssoker {
        return populerArbeidssøker(foedselsnummer)
    }

    private fun populerArbeidssøker(gjeldendeFoedselsnummer: Foedselsnummer, historiskeFoedselsnummer: List<Foedselsnummer> = emptyList()): Arbeidssoker {
        val alleFoedselsnummer = historiskeFoedselsnummer + gjeldendeFoedselsnummer
        val formidlingsgruppe =
            formidlingsgruppeRepository.finnFormidlingsgruppeEndretEventFor(alleFoedselsnummer)
        val ordinaerBrukerRegistreringer =
            brukerRegistreringRepository.hentBrukerregistreringForFoedselsnummer(alleFoedselsnummer)
        val reaktiveringer =
            brukerReaktiveringRepository.finnReaktiveringerForFoedselsnummer(alleFoedselsnummer)

        //val listeMedArbeidssøkerEndringer =
          //  filterBortIkkeAktivePersonIdOgTekniskeISERVEndringer(formidlingsgruppe) + ordinaerBrukerRegistreringer + reaktiveringer
        val listeMedArbeidssøkerEndringer =
            filtrerBortIkkeAktivPersonIdOgInitielleISERVInserts(formidlingsgruppe) + ordinaerBrukerRegistreringer + reaktiveringer

        val arbeidssoker = Arbeidssoker(gjeldendeFoedselsnummer)

        listeMedArbeidssøkerEndringer
            .sortedBy { it.opprettetTidspunkt() }
            .forEach { arbeidssoker.behandle(it) }

        return arbeidssoker
    }

}