package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.kafka

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.Meldekorttype
import no.nav.fo.veilarbregistrering.config.objectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MeldekortEventDtoTest {

    @Test
    fun skalDeserialisereMeldingTilDto() {
        val meldekortMelding = toJson("/kafka/meldekort.json")

        val meldekortEventDto = objectMapper.readValue(meldekortMelding, MeldekortEventDto::class.java)

        assertNotNull(meldekortEventDto)
    }

    @Test
    fun skalMappeDtoTilInterntFormat() {
        val meldekortEventDto = objectMapper.readValue(toJson("/kafka/meldekort.json"), MeldekortEventDto::class.java)

        val meldekortEvent = meldekortEventDto.map()

        assertNotNull(meldekortEvent)
        assertEquals(Meldekorttype.MANUELL_ARENA, meldekortEvent.meldekorttype)
    }
}