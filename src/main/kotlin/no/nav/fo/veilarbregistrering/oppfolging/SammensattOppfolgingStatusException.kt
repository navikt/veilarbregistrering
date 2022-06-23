package no.nav.fo.veilarbregistrering.oppfolging


class SammensattOppfolgingStatusException: RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, exception: Exception) : super(message, exception)
}
