package no.nav.fo.veilarbregistrering.postgres.migrering

data class Tabellsjekk(val tabellNavn: TabellNavn, val ok: Boolean, val feilendeKolonner: List<String>)