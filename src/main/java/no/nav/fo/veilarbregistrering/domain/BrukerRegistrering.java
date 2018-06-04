package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ToString
public class BrukerRegistrering {
    long id;
    String nusKode;
    String yrkesPraksis;
    Date opprettetDato;
    boolean enigIOppsummering;
    String oppsummering;
    boolean harHelseutfordringer;
    AktiverBrukerResponseStatus aktiverBrukerResponseStatus;
}
