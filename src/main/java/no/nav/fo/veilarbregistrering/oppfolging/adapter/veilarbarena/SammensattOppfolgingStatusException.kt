package no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena


class SammensattOppfolgingStatusException: RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, exception: Exception) : super(message, exception)
}
