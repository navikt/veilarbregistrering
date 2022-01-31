package no.nav.fo.veilarbregistrering.bruker

class Person (
    val telefonnummer: Telefonnummer?,
    val foedselsdato: Foedselsdato?,
    val adressebeskyttelseGradering: AdressebeskyttelseGradering?,
    val navn: Navn
) {

    fun harAdressebeskyttelse(): Boolean =
        adressebeskyttelseGradering != null && adressebeskyttelseGradering.erGradert()
}