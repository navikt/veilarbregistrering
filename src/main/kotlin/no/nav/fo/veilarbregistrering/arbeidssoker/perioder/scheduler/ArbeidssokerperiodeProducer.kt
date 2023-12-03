package no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler

interface ArbeidssokerperiodeProducer {
    fun publiserArbeidssokerperioder(arbeidssokerperioder: ArbeidssokerperiodeHendelseMelding): Boolean
}
