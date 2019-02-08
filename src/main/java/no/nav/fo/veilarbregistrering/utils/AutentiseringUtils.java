package no.nav.fo.veilarbregistrering.utils;

import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.auth.SubjectHandler;

import java.util.Optional;

public class AutentiseringUtils {

    public static boolean erEksternBruker() {
        IdentType identType = SubjectHandler.getIdentType().orElse(null);
        return IdentType.EksternBruker.equals(identType);
    }

    public static boolean erInternBruker() {
        IdentType identType = SubjectHandler.getIdentType().orElse(null);
        return IdentType.InternBruker.equals(identType);
    }

    public static Optional<String> hentIdent() {
        return SubjectHandler.getIdent();
    }

}
