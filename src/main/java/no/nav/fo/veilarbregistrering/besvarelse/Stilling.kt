package no.nav.fo.veilarbregistrering.besvarelse

data class Stilling(
    val label: String? = null,
    val konseptId: Long = 0,
    val styrk08: String? = null,
) {
    override fun toString(): String {
        return "Stilling(label=$label, konseptId=$konseptId, styrk08=$styrk08)"
    }
}

fun tomStilling(): Stilling {
    return Stilling("", -1L, "-1")
}

val ingenYrkesbakgrunn: Stilling = Stilling("X", -1L, "X")
