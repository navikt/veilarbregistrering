package no.nav.fo.veilarbregistrering.oppfolging

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

interface OppfolgingGateway {

    fun hentOppfolgingsstatus(fodselsnummer: Foedselsnummer): Oppfolgingsstatus
    fun aktiverBruker(foedselsnummer: Foedselsnummer, innsatsgruppe: Innsatsgruppe)
    fun reaktiverBruker(fodselsnummer: Foedselsnummer)
    fun aktiverSykmeldt(fodselsnummer: Foedselsnummer, besvarelse: Besvarelse)
    fun hentOppfolgingsstatusFraNyeKilder(fodselsnummer: Foedselsnummer): Oppfolgingsstatus
}