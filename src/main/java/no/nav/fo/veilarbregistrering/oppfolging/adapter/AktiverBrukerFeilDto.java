package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class AktiverBrukerFeilDto {
    private String id;
    private ArenaFeilType type;
    private DetaljerDto detaljer;

    public String getId() {
        return id;
    }

    public ArenaFeilType getType() {
        return type;
    }

    public Optional<DetaljerDto> getDetaljer() {
        return ofNullable(detaljer);
    }

    public enum ArenaFeilType {
        BRUKER_ER_UKJENT,
        BRUKER_KAN_IKKE_REAKTIVERES,
        BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET,
        BRUKER_MANGLER_ARBEIDSTILLATELSE
    }

    @Override
    public String toString() {
        return "AktiverBrukerFeil{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", detaljer='" + detaljer + '\'' +
                '}';
    }
}
