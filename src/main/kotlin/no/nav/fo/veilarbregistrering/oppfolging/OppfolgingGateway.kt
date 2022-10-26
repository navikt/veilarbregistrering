package no.nav.fo.veilarbregistrering.oppfolging

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

interface OppfolgingGateway {

    fun hentOppfolgingsstatus(fodselsnummer: Foedselsnummer): Oppfolgingsstatus
    fun kanReaktiveres(fodselsnummer: Foedselsnummer): Boolean?
    fun erUnderOppfolging(fodselsnummer: Foedselsnummer): Boolean
    fun arenaStatus(fodselsnummer: Foedselsnummer): ArenaStatus?

    fun aktiverBruker(foedselsnummer: Foedselsnummer, innsatsgruppe: Innsatsgruppe)
    fun reaktiverBruker(fodselsnummer: Foedselsnummer)
    fun aktiverSykmeldt(fodselsnummer: Foedselsnummer, besvarelse: Besvarelse)
}