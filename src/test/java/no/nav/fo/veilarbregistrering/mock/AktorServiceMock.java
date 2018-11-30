package no.nav.fo.veilarbregistrering.mock;

import no.nav.dialogarena.aktor.AktorService;
import no.nav.dialogarena.aktor.AktorServiceImpl;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class AktorServiceMock extends AktorServiceImpl implements AktorService {

    @Override
    public Optional<String> getFnr(String s) {
        return ofNullable(s);
    }

    @Override
    public Optional<String> getAktorId(String s) {
        return ofNullable("1284181123913");
    }

}
