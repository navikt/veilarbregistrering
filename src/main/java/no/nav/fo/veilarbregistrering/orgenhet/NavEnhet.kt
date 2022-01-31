package no.nav.fo.veilarbregistrering.orgenhet

data class NavEnhet(val id: String, val navn: String) {

    override fun toString(): String {
        return "NavEnhet(id=" + id + ", navn=" + navn + ")"
    }
}