package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.bruker.feil.KontaktinfoIngenTilgang
import no.nav.fo.veilarbregistrering.bruker.feil.KontaktinfoIngenTreff
import no.nav.fo.veilarbregistrering.bruker.feil.KontaktinfoUkjentFeil
import no.nav.fo.veilarbregistrering.feil.FeilType
import org.slf4j.LoggerFactory
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAuthorizedException

class KontaktinfoService(private val pdlOppslagGateway: PdlOppslagGateway, private val krrGateway: KrrGateway) {
    fun hentKontaktinfo(bruker: Bruker): Kontaktinfo {
        val feiltyper: MutableList<FeilType> = ArrayList(2)
        val person: Person? = try {
            pdlOppslagGateway.hentPerson(bruker.aktorId)
        } catch (e: RuntimeException) {
            LOG.error("Hent kontaktinfo fra PDL feilet", e)
            feiltyper.add(FeilType.UKJENT)
            null
        }
        val telefonnummer: Telefonnummer? = try {
            krrGateway.hentKontaktinfo(bruker)
        } catch (e: NotAuthorizedException) {
            LOG.error("Hent kontaktinfo fra Kontakt og reservasjonsregisteret feilet pga manglende autentisering", e)
            feiltyper.add(FeilType.INGEN_TILGANG)
            null
        } catch (e: ForbiddenException) {
            LOG.error("Hent kontaktinfo fra Kontakt og reservasjonsregisteret feilet pga manglende tilgang", e)
            feiltyper.add(FeilType.INGEN_TILGANG)
            null
        } catch (e: RuntimeException) {
            LOG.error("Hent kontaktinfo fra Kontakt og reservasjonsregisteret feilet av ukjent grunn", e)
            feiltyper.add(FeilType.UKJENT)
            null
        }
        if (fantMinstEttTelefonnummer(person, telefonnummer)) {
            return opprettKontaktinfo(person, telefonnummer)
        }
        if (feiltyper.contains(FeilType.INGEN_TILGANG)) {
            throw KontaktinfoIngenTilgang()
        }
        if (feiltyper.contains(FeilType.UKJENT)) {
            throw KontaktinfoUkjentFeil()
        }
        throw KontaktinfoIngenTreff()
    }

    private fun fantMinstEttTelefonnummer(person: Person?, telefonnummer: Telefonnummer?): Boolean {
        return (person?.telefonnummer?.isPresent ?: false)
                || telefonnummer != null
    }

    private fun opprettKontaktinfo(person: Person?, telefonnummer: Telefonnummer?): Kontaktinfo {
        return Kontaktinfo.of(
            (person?.telefonnummer?.orElseGet { null })
                ?.let { obj: Telefonnummer -> obj.asLandkodeOgNummer() },
            telefonnummer,
            person?.navn
        )
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(KontaktinfoService::class.java)
    }
}