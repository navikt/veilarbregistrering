package no.nav.fo.veilarbregistrering.bruker;

import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;

import java.util.Optional;

public class AutentiseringUtils {

    public static boolean erVeileder() {
        // Dette er en midlertidig hack for å fikse en bug i commons som
        // gjør at IdentType.InternBruker blir returnert for systembruker.
        // Se PUS-254.
        Subject subject = SubjectHandler.getSubject().orElse(null);
        return subject != null && IdentType.InternBruker.equals(subject.getIdentType()) && erVeilederIdent(subject.getUid());
    }

    public static Optional<String> hentIdent() {
        return SubjectHandler.getIdent();
    }

    private static boolean erVeilederIdent(String ident) {
        return ident != null && ident.matches("^[a-zA-Z]\\d+$");
    }

}
