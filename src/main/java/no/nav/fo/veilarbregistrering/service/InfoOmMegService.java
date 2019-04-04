package no.nav.fo.veilarbregistrering.service;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.db.InfoOmMegRepository;
import no.nav.fo.veilarbregistrering.domain.AktorId;
import no.nav.fo.veilarbregistrering.domain.FremtidigSituasjonData;
import no.nav.fo.veilarbregistrering.domain.SykmeldtRegistrering;


public class InfoOmMegService {
    private final ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private final InfoOmMegRepository infoOmMegRepository;

    public InfoOmMegService(ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
                            InfoOmMegRepository infoOmMegRepository) {
        this.arbeidssokerregistreringRepository = arbeidssokerregistreringRepository;
        this.infoOmMegRepository = infoOmMegRepository;
    }

    public FremtidigSituasjonData hentFremtidigSituasjon(AktorId aktorId) {
        FremtidigSituasjonData fremtidigSituasjonData = infoOmMegRepository.hentFremtidigSituasjonForAktorId(aktorId);

        if (fremtidigSituasjonData == null) {
            SykmeldtRegistrering sykmeldtBrukerRegistrering = arbeidssokerregistreringRepository
                    .hentSykmeldtregistreringForAktorId(aktorId);

            if (sykmeldtBrukerRegistrering == null) {
                return null;
            }

            return new FremtidigSituasjonData()
                        .setAlternativId(sykmeldtBrukerRegistrering.getBesvarelse().getFremtidigSituasjon());

       }
       return fremtidigSituasjonData;
    }

    public void lagreFremtidigSituasjon(FremtidigSituasjonData fremtidigSituasjon, AktorId aktorId, String endretAv) {
        infoOmMegRepository.lagreFremtidigSituasjonForAktorId(fremtidigSituasjon, aktorId, endretAv);

    }
}
