package no.nav.paw.arbeidssokerregisteret.intern.v1.vo

data class JobbsituasjonMedDetaljer(
    val beskrivelse: JobbsituasjonBeskrivelse,
    val detaljer: Map<String, String>
)