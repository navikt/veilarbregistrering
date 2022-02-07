package no.nav.fo.veilarbregistrering.bruker.feil

import no.nav.fo.veilarbregistrering.bruker.pdl.PdlError

class PdlException(melding: String, pdlErrors: List<PdlError>): RuntimeException("$melding ($pdlErrors)")
