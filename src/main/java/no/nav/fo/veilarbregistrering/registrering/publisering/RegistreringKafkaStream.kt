package no.nav.fo.veilarbregistrering.registrering.publisering

class RegistreringKafkaStream(
    private val arbeidssokerRegistrertProducerAiven: ArbeidssokerRegistrertProducer,
    private val arbeidssokerProfilertProducerAiven: ArbeidssokerProfilertProducer,
) {
    fun publiserMeldingerForRegistreringer() {
        //Les onprem registrert topic
        //deserialiser
        //publiser på aiven

        //les onprem profilert
        //deserialiser
        //publiser på aiven
    }
}