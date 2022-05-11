package no.nav.fo.veilarbregistrering.oppfolging

class ReaktiverBrukerException: RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, exception: Exception) : super(message, exception)
}