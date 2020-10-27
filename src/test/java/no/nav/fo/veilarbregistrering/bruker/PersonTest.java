package no.nav.fo.veilarbregistrering.bruker;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    @Test
    public void harAdressebeskyttelse() {
        assertFalse(personMedAdressebeskyttelseGradering(null).harAdressebeskyttelse());
        assertFalse(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.UKJENT).harAdressebeskyttelse());
        assertFalse(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.UGRADERT).harAdressebeskyttelse());

        assertTrue(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.FORTROLIG).harAdressebeskyttelse());
        assertTrue(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.STRENGT_FORTROLIG).harAdressebeskyttelse());
        assertTrue(personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND).harAdressebeskyttelse());
    }

    private Person personMedAdressebeskyttelseGradering(AdressebeskyttelseGradering gradering) {
        return Person.of(null, null, null, null, null, gradering);
    }

}