package no.nav.fo.veilarbregistrering.bruker;

public interface AktorGateway {

    AktorId hentAktorIdFor(Foedselsnummer fnr);

    Foedselsnummer hentFnrFor(AktorId aktorId);

}
