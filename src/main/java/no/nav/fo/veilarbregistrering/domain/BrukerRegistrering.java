package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BrukerRegistrering {
    private String nusKode;
    private String yrkesPraksis;
    private boolean enigIOppsummering;
    private String oppsummering;
    private boolean harHelseutfordringer;
}
