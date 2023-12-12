package no.nav.paw.arbeidssokerregisteret.intern.v1.vo

import no.nav.paw.arbeidssokerregisteret.intern.v1.HarMetadata
import java.util.*
data class OpplysningerOmArbeidssoeker(
    val id: UUID,
    override val metadata: Metadata,
    val utdanning: Utdanning,
    val helse: Helse,
    val arbeidserfaring: Arbeidserfaring,
    val jobbsituasjon: Jobbsituasjon,
    val annet: Annet
) : HarMetadata
