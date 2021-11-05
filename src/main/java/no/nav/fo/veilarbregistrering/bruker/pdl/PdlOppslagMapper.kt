package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.bruker.Identer.Companion.of
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.*

internal object PdlOppslagMapper {

    @JvmStatic
    fun map(pdlGeografiskTilknytning: PdlGeografiskTilknytning?): GeografiskTilknytning? {
        if (pdlGeografiskTilknytning == null) {
            return null
        }
        return when (pdlGeografiskTilknytning.gtType) {
            PdlGtType.BYDEL -> return GeografiskTilknytning.of(pdlGeografiskTilknytning.gtBydel)
            PdlGtType.KOMMUNE -> return GeografiskTilknytning.of(pdlGeografiskTilknytning.gtKommune)
            PdlGtType.UTLAND -> {
                val gtLand = pdlGeografiskTilknytning.gtLand
                return if (gtLand != null) GeografiskTilknytning.of(gtLand) else GeografiskTilknytning.ukjentBostedsadresse()
            }

            else -> null
        }
    }

    @JvmStatic
    fun map(pdlPerson: PdlPerson): Person {
        return Person.of(
            pdlPerson.hoyestPrioriterteTelefonnummer()
                .map { obj: PdlTelefonnummer -> map(obj) }
                .orElse(null),
            pdlPerson.getSistePdlFoedsel()
                .map { obj: PdlFoedsel -> map(obj) }
                .orElse(null),
            pdlPerson.strengesteAdressebeskyttelse()
                .map { obj: PdlAdressebeskyttelse -> map(obj) }
                .orElse(AdressebeskyttelseGradering.UKJENT))
    }

    private fun map(pdlFoedsel: PdlFoedsel): Foedselsdato {
        return Foedselsdato(pdlFoedsel.foedselsdato)
    }

    private fun map(pdlTelefonnummer: PdlTelefonnummer): Telefonnummer {
        return Telefonnummer.of(pdlTelefonnummer.nummer, pdlTelefonnummer.landskode)
    }

    @JvmStatic
    fun map(pdlIdenter: PdlIdenter): Identer {
        return of(pdlIdenter.identer.map { (ident, historisk, gruppe) ->
                Ident(
                    ident,
                    historisk,
                    Gruppe.valueOf(gruppe.name)
                )
            })
    }

    @JvmStatic
    fun map(adressebeskyttelse: PdlAdressebeskyttelse): AdressebeskyttelseGradering {
        return AdressebeskyttelseGradering.valueOf(adressebeskyttelse.gradering.name)
    }
}
