package no.nav.fo.veilarbregistrering.oppfolging

/**
 * Servicegruppe er en undergruppe (?) til Kvalifiseringsgruppe sammen med Innsatsgruppe.
 * Begrepene brukes litt på tvers av hverandre, men referer til det samme feltet i Arena.
 * ----------------------------------
 * Arbeidsrettet oppfølging fra Modia
 * ----------------------------------
 * IKVAL - Standard Innsats
 * BFORM - Situasjonsbestemt innsats
 * BATT  - Spesielt tilpasset innsats
 * VARIG - Varig tilpasset innsats
 * ----------------------------------
 * Sykefraværsoppfølging
 * ----------------------------------
 * OPPFI - Helserelatert arbeidsrettet oppfølging i NAV
 * VURDI - Sykmeldt oppfølging på arbeidsplassen
 * VURDU - Sykmeldt uten arbeidsgiver
 * ----------------------------------
 * Aktivering fra Modia og sykefraværsoppfølging
 * ----------------------------------
 * IVURD - Ikke vurdert
 */
data class Servicegruppe(val kode: String) {

    override fun toString(): String = "servicegruppe='$kode'"
}