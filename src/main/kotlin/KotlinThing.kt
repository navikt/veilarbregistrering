package no.nav.fo.veilarbregistrering;

import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlAdressebeskyttelse
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlGradering
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson

object KotlinThing {

    fun kotlinPerson(): PdlPerson {
        val person = PdlPerson()
        person.adressebeskyttelse = listOf(PdlAdressebeskyttelse(PdlGradering.FORTROLIG))
        return person
    }
}