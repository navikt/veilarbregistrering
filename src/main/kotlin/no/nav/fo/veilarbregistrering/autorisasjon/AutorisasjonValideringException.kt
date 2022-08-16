package no.nav.fo.veilarbregistrering.autorisasjon

class AutorisasjonValideringException(val melding: String) : RuntimeException(melding)