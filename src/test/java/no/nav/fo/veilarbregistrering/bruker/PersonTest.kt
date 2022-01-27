package no.nav.fo.veilarbregistrering.bruker

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


val testNavn = Navn("Ola", null, "Normann")

class PersonTest {

    @Test
    fun `skal ha adressebeskyttelse`() {
        assertTrue(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.FORTROLIG).harAdressebeskyttelse())
        assertTrue(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.STRENGT_FORTROLIG).harAdressebeskyttelse())
        assertTrue(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND).harAdressebeskyttelse())
    }

    @Test
    fun `skal ikke ha adressebeskyttelse`() {
        assertFalse(personMedAdressebeskyttelseGradering(null).harAdressebeskyttelse())
        assertFalse(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.UKJENT).harAdressebeskyttelse())
        assertFalse(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.UGRADERT).harAdressebeskyttelse())
    }

    private fun personMedAdressebeskyttelseGradering(gradering: AdressebeskyttelseGradering?): Person {
        return Person.of(null, null, gradering, testNavn)
    }
}
