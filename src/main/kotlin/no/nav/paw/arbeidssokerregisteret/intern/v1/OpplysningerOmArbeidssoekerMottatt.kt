package no.nav.paw.arbeidssokerregisteret.intern.v1

import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Metadata
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.OpplysningerOmArbeidssoeker
import java.util.*

data class OpplysningerOmArbeidssoekerMottatt(
    override val hendelseId: UUID,
    override val identitetsnummer: String,
    val opplysningerOmArbeidssoeker: OpplysningerOmArbeidssoeker
) : Hendelse {
    override val hendelseType: String = opplysningerOmArbeidssoekerHendelseType
    override val metadata: Metadata = opplysningerOmArbeidssoeker.metadata
}