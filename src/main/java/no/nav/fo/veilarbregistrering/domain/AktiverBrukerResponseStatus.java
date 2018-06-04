package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AktiverBrukerResponseStatus {

    private Status status;

    public AktiverBrukerResponseStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        INGEN_STATUS("ingen_status"),
        STATUS_SUKSESS("status_sukess"),
        BRUKER_ER_UKJENT("bruker_er_ukjent"),
        BRUKER_KAN_IKKE_REAKTIVERES("bruker_kan_ikke_reaktiveres"),
        BRUKER_MANGLER_ARBEIDSTILLATELSE("bruker_mangler_arbeidstillatelse"),
        BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET("bruker_er_dod_utvandret_eller_forsvunnet");

        public final String status;

        Status(String status) {
            this.status = status;
        }
    }

}
