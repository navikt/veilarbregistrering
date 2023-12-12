package no.nav.fo.veilarbregistrering.registrering.ordinaer.scheduler

import no.nav.paw.arbeidssokerregisteret.intern.v1.OpplysningerOmArbeidssoekerMottatt

interface OpplysningerMottattProducer {
    fun publiserOpplysningerMottatt(opplysningerOmArbeidssoekerMottatt: OpplysningerOmArbeidssoekerMottatt): Boolean
}
