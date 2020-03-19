package no.nav.fo.veilarbregistrering.oppfolging;

import java.util.Optional;

import static java.util.Optional.ofNullable;

enum ArenaFeilType {
    BRUKER_ER_UKJENT,
    BRUKER_KAN_IKKE_REAKTIVERES,
    BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET,
    BRUKER_MANGLER_ARBEIDSTILLATELSE
}

public class AktiverBrukerFeil {
    private String id;
    private ArenaFeilType type;
    private String detaljer;

    public String getId() {return id;}

    public ArenaFeilType getType() {return type;}

    public Optional<String> getDetaljer() {return ofNullable(detaljer);}
}
