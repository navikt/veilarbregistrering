package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.Person;

import java.time.LocalDate;
import java.util.Optional;

class PdlOppslagGatewayImpl implements PdlOppslagGateway {

    private PdlOppslagClient pdlOppslagClient;

    PdlOppslagGatewayImpl(PdlOppslagClient pdlOppslagClient) {
        this.pdlOppslagClient = pdlOppslagClient;
    }

    @Override
    public Person hentPerson(AktorId aktorid) {
        return map(pdlOppslagClient.hentPerson(aktorid));
    }

    private Person map(Optional<PdlPerson> person) {
        return person.map(pdlPerson ->
                Person.of(
                        pdlPerson.getSisteOpphold()
                                .map(PdlOppslagGatewayImpl::map)
                                .orElse(null),
                        pdlPerson.getSisteStatsborgerskap()
                                .map(PdlOppslagGatewayImpl::map)
                                .orElse(null))
        ).orElse(null);
    }

    private static Person.Opphold map(PdlPersonOpphold pdlPersonOpphold) {
        return Person.Opphold.of(
                Person.Oppholdstype.valueOf(pdlPersonOpphold.getType().name()),
                mapPeriode(pdlPersonOpphold.getOppholdFra(), pdlPersonOpphold.getOppholdTil()));
    }

    private static Person.Statsborgerskap map(PdlStatsborgerskap pdlStatsborgerskap) {
        return Person.Statsborgerskap.of(
                pdlStatsborgerskap.getLand(),
                mapPeriode(pdlStatsborgerskap.getGyldigFraOgMed(), pdlStatsborgerskap.getGyldigTilOgMed()));
    }

    private static Person.Periode mapPeriode(LocalDate gyldigFraOgMed, LocalDate gyldigTilOgMed) {
        if (gyldigFraOgMed == null && gyldigTilOgMed == null) {
            return null;
        }

        return Person.Periode.of(
                gyldigFraOgMed,
                gyldigTilOgMed);
    }
}
