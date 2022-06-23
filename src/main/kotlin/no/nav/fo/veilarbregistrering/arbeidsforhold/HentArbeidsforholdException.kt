package no.nav.fo.veilarbregistrering.arbeidsforhold

class HentArbeidsforholdException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, exception: Exception) : super(message, exception)
}