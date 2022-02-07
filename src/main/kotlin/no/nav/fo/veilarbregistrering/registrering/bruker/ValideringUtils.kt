package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.besvarelse.*


object ValideringUtils {
    private val situasjonerDerViVetAtBrukerenHarHattJobb = listOf(
        DinSituasjonSvar.MISTET_JOBBEN,
        DinSituasjonSvar.HAR_SAGT_OPP,
        DinSituasjonSvar.ER_PERMITTERT,
        DinSituasjonSvar.DELTIDSJOBB_VIL_MER,
        DinSituasjonSvar.VIL_BYTTE_JOBB,
        DinSituasjonSvar.VIL_FORTSETTE_I_JOBB
    )
    private val situasjonerDerViVetAtBrukerenIkkeHarHattJobb: List<DinSituasjonSvar?> = listOf(
        DinSituasjonSvar.ALDRI_HATT_JOBB
    )

    @JvmStatic
    fun validerBrukerRegistrering(bruker: OrdinaerBrukerRegistrering) {
        val (utdanningSvar, utdanningBestatt, utdanningGodkjent, helseHinder, andreForhold, sisteStillingSvar, dinSituasjonSvar) = bruker.besvarelse
        assertFalse(besvarelseHarNull(bruker))
        assertFalse(stillingHarNull(bruker))
        assertFalse(helseHinder == HelseHinderSvar.INGEN_SVAR)
        assertFalse(andreForhold == AndreForholdSvar.INGEN_SVAR)
        assertFalse(bruker.sisteStilling == tomStilling())
        stillingSkalSamsvareMedSisteStillingSpm(bruker)
        assertBothTrueOrBothFalse(
            situasjonerDerViVetAtBrukerenHarHattJobb.contains(dinSituasjonSvar),
            brukerHarYrkesbakgrunn(bruker) && SisteStillingSvar.INGEN_SVAR == sisteStillingSvar
        )
        assertBothTrueOrBothFalse(
            situasjonerDerViVetAtBrukerenIkkeHarHattJobb.contains(dinSituasjonSvar),
            !brukerHarYrkesbakgrunn(bruker) && SisteStillingSvar.INGEN_SVAR == sisteStillingSvar
        )
        assertBothTrueOrBothFalse(
            DinSituasjonSvar.VIL_FORTSETTE_I_JOBB == dinSituasjonSvar,
            UtdanningSvar.INGEN_SVAR == utdanningSvar
        )
        assertBothTrueOrBothFalse(
            UtdanningSvar.INGEN_SVAR == utdanningSvar || UtdanningSvar.INGEN_UTDANNING == utdanningSvar,
            UtdanningBestattSvar.INGEN_SVAR == utdanningBestatt && UtdanningGodkjentSvar.INGEN_SVAR == utdanningGodkjent
        )
        assertBothTrueOrBothFalse(
            UtdanningSvar.INGEN_UTDANNING == utdanningSvar,
            DinSituasjonSvar.VIL_FORTSETTE_I_JOBB != dinSituasjonSvar
                    && UtdanningBestattSvar.INGEN_SVAR == utdanningBestatt && UtdanningGodkjentSvar.INGEN_SVAR == utdanningGodkjent
        )
    }

    private fun stillingSkalSamsvareMedSisteStillingSpm(bruker: OrdinaerBrukerRegistrering) {
        val sisteStillingSvar = bruker.besvarelse.sisteStilling
        if (SisteStillingSvar.HAR_HATT_JOBB == sisteStillingSvar) {
            assertTrue(brukerHarYrkesbakgrunn(bruker))
        } else if (SisteStillingSvar.HAR_IKKE_HATT_JOBB == sisteStillingSvar) {
            assertFalse(brukerHarYrkesbakgrunn(bruker))
        }
    }

    private fun stillingHarNull(bruker: OrdinaerBrukerRegistrering): Boolean {
        val stilling = bruker.sisteStilling
        return (isEmpty(stilling.styrk08)
                || isEmpty(stilling.label))
    }

    private fun besvarelseHarNull(bruker: OrdinaerBrukerRegistrering): Boolean {
        val besvarelse = bruker.besvarelse
        return besvarelse.dinSituasjon == null || besvarelse.sisteStilling == null || besvarelse.utdanning == null || besvarelse.utdanningGodkjent == null || besvarelse.utdanningBestatt == null || besvarelse.helseHinder == null || besvarelse.andreForhold == null
    }

    private fun brukerHarYrkesbakgrunn(bruker: OrdinaerBrukerRegistrering): Boolean {
        return bruker.sisteStilling != ingenYrkesbakgrunn
    }

    private fun assertBothTrueOrBothFalse(value1: Boolean, value2: Boolean) {
        assertTrue(value1 == value2)
    }

    private fun assertTrue(value: Boolean) {
        if (!value) {
            throw RuntimeException("Registreringsinformasjonen er ugyldig.")
        }
    }

    private fun assertFalse(value: Boolean) {
        assertTrue(!value)
    }

    private fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }
}