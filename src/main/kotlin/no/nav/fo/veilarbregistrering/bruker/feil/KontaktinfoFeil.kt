package no.nav.fo.veilarbregistrering.bruker.feil

class KontaktinfoIngenTilgang(val melding: String) : RuntimeException(melding)

class KontaktinfoIngenTreff(val melding: String) : RuntimeException(melding)

class KontaktinfoUkjentFeil(val melding: String) : RuntimeException(melding)