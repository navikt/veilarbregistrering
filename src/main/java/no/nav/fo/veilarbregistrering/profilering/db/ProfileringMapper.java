package no.nav.fo.veilarbregistrering.profilering.db;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;

import java.sql.ResultSet;

class ProfileringMapper {

    @SneakyThrows
    static Profilering map(ResultSet rs) {

        Profilering profilering = new Profilering();

        do {
            switch (rs.getString(ProfileringRepositoryImpl.PROFILERING_TYPE)){
                case ProfileringRepositoryImpl.ALDER:
                    profilering.setAlder(rs.getInt(ProfileringRepositoryImpl.VERDI));
                    break;
                case ProfileringRepositoryImpl.ARB_6_AV_SISTE_12_MND:
                    profilering.setJobbetSammenhengendeSeksAvTolvSisteManeder(rs.getBoolean(ProfileringRepositoryImpl.VERDI));
                    break;
                case ProfileringRepositoryImpl.RESULTAT_PROFILERING:
                    profilering.setInnsatsgruppe(Innsatsgruppe.tilInnsatsgruppe(rs.getString(ProfileringRepositoryImpl.VERDI)));
                    break;
            }

        } while (rs.next());

        return profilering;
    }
}
