package no.nav.fo.veilarbregistrering.db.profilering;

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;

import java.sql.ResultSet;
import java.sql.SQLException;

class ProfileringMapper {

    static Profilering map(ResultSet rs) {

        try {
            Profilering profilering = new Profilering();

            do {
                switch (rs.getString(ProfileringRepositoryImpl.PROFILERING_TYPE)) {
                    case ProfileringRepositoryImpl.ALDER:
                        profilering.setAlder(rs.getInt(ProfileringRepositoryImpl.VERDI));
                        break;
                    case ProfileringRepositoryImpl.ARB_6_AV_SISTE_12_MND:
                        profilering.setJobbetSammenhengendeSeksAvTolvSisteManeder(rs.getBoolean(ProfileringRepositoryImpl.VERDI));
                        break;
                    case ProfileringRepositoryImpl.RESULTAT_PROFILERING:
                        profilering.setInnsatsgruppe(Innsatsgruppe.of(rs.getString(ProfileringRepositoryImpl.VERDI)));
                        break;
                }

            } while (rs.next());

            return profilering;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}