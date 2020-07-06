package no.nav.fo.veilarbregistrering.bruker.pdl;

import java.util.List;

public interface PdlResponse {
    List<PdlError> getErrors();
}
