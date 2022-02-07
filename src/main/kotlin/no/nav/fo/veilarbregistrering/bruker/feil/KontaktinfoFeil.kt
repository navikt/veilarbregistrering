package no.nav.fo.veilarbregistrering.bruker.feil

class KontaktinfoIngenTilgang : RuntimeException("Ingen tilgang ved kall til PDL")

class KontaktinfoIngenTreff : RuntimeException("Ingen treff ved oppslag i PDL")

class KontaktinfoUkjentFeil : RuntimeException("Ukjent feil ved henting av kontaktinfo fra PDL")