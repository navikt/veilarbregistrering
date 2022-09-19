package no.nav.fo.veilarbregistrering.migrering.konsument

import no.nav.fo.veilarbregistrering.migrering.TabellNavn

data class Tabellsjekk(val tabellNavn: TabellNavn, val ok: Boolean, val feilendeKolonner: List<String>)