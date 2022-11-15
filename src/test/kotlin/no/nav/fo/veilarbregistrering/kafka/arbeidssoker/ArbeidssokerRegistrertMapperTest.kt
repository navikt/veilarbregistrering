package no.nav.fo.veilarbregistrering.kafka.arbeidssoker

import no.nav.arbeid.soker.registrering.UtdanningBestattSvar
import no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar
import no.nav.arbeid.soker.registrering.UtdanningSvar
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder.gyldigBesvarelse
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.kafka.arbeidsssoker.ArbeidssokerRegistrertMapper
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertInternalEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ArbeidssokerRegistrertMapperTest {
    @Test
    fun `skal mappe opp alle verdier`() {
        val arbeidssokerRegistrertEvent = ArbeidssokerRegistrertMapper.map(
            ArbeidssokerRegistrertInternalEvent(
                AktorId("123"),
                gyldigBesvarelse(dinSituasjon = DinSituasjonSvar.ER_PERMITTERT),
                LocalDateTime.now()
            )
        )
        assertThat(arbeidssokerRegistrertEvent.getAktorid()).isEqualTo("123")
        assertThat(arbeidssokerRegistrertEvent.getUtdanning())
            .isEqualTo(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER)
        assertThat(arbeidssokerRegistrertEvent.getUtdanningBestatt()).isEqualTo(UtdanningBestattSvar.JA)
        assertThat(arbeidssokerRegistrertEvent.getUtdanningGodkjent()).isEqualTo(UtdanningGodkjentSvar.JA)
        assertThat(arbeidssokerRegistrertEvent.getBrukersSituasjon()).isEqualTo("ER_PERMITTERT")
        assertThat(arbeidssokerRegistrertEvent.getRegistreringOpprettet()).isNotNull
    }
}
