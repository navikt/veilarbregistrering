package no.nav.fo.veilarbregistrering.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class BrukerRegistrering {
    long id;
    String nusKode;
    String yrkesPraksis;
    Date opprettetDato;
    boolean enigIOppsummering;
    String oppsummering;
    boolean harHelseutfordringer;
}
