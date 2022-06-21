package no.nav.fo.veilarbregistrering.registrering.ordinaer.resources

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDto
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering

@Tag(name = "OrdinaerBrukerRegistreringResource")
interface OrdinaerBrukerRegistreringApi {

    @Operation(
        summary = "Registrerer bruker som av arbeidssøker.",
        description = "Tjenesten persisterer svaret fra bruker, og aktiverer bruker som arbeidssøker " +
                "(formidlingsgruppe=ARBS) i Arena (via veilarboppfolging). Hvis aktiveringen i Arena blir vellykket, " +
                "varsles omgivelsene ved å publisere to hendelser på Kafka om at arbeidsøker er registrert og at arbeidssøker er profilert.",
        externalDocs = ExternalDocumentation(
            description = "Arena - Tjeneste Webservice - BehandleArbeidssoeker_v1",
            url = "https://confluence.adeo.no/display/ARENA/Arena+-+Tjeneste+Webservice+-+BehandleArbeidssoeker_v1#ArenaTjenesteWebserviceBehandleArbeidssoeker_v1-Funksjonellbeskrivelse"
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Registrering OK"),
            ApiResponse(responseCode = "500", description = "Registrering feilet. \n" +
                    "Sjekk *AktiverBrukerFeil* for nærmere detaljer om årsak. Kan ha verdiene: \n" +
                    "- BRUKER_ER_UKJENT: Kastes dersom identen til brukeren ikke finnes registrert i Fellesregistre (AktørID/TPS/NORG). Brukeren kan da heller ikke aktiveres/reaktiveres i Arena.\n" +
                    "- BRUKER_KAN_IKKE_REAKTIVERES: Bruker kan ikke reaktiveres siden bruker har status som aktivert.\n" +
                    "- BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET: Bruker kan ikke aktiveres siden bruker er markert som død/utvandret/forsvunnet.\n" +
                    "- BRUKER_MANGLER_ARBEIDSTILLATELSE: Bruker kan ikke aktiveres automatisk siden bruker ikke har arbeidstillatelse.")
        ])
    fun registrerBruker(ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering): OrdinaerBrukerRegistrering
}