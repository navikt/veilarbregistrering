package no.nav.fo.veilarbregistrering.oppgave

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import java.time.LocalDate
import java.util.*

data class Oppgave(
    val aktorId: AktorId,
    val enhetnr: Enhetnr?,
    private val oppgaveType: OppgaveType,
    val fristFerdigstillelse: LocalDate,
    val aktivDato: LocalDate
) {
    val beskrivelse: String
        get() = OppgaveBeskrivelse.from(oppgaveType)

    companion object {
        fun opprettOppgave(
            aktorId: AktorId,
            enhetnr: Enhetnr?,
            oppgaveType: OppgaveType,
            dagensDato: LocalDate
        ): Oppgave {
            return Oppgave(aktorId, enhetnr, oppgaveType, fristFerdigstillelse(dagensDato), dagensDato)
        }

        private fun fristFerdigstillelse(dagensDato: LocalDate): LocalDate {
            return Virkedager.plussAntallArbeidsdager(dagensDato, 2)
        }
    }

}

enum class OppgaveBeskrivelse(val key: OppgaveType, val tekst: String) {
    OPPHOLDSTILLATELSE(OppgaveType.OPPHOLDSTILLATELSE, """
                Brukeren får ikke registrert seg som arbeidssøker pga. manglende oppholdstillatelse i Arena, og har selv opprettet denne oppgaven.
                
                Følg rutinen som er beskrevet for registreringen av arbeids- og oppholdstillatelse: https://navno.sharepoint.com/sites/fag-og-ytelser-regelverk-og-rutiner/SitePages/Registrering-av-arbeids--og-oppholdstillatelse.aspx
                
                Har oppgaven havnet i feil oppgaveliste? Da ønsker vi som har utviklet løsningen tilbakemelding på dette. Meld sak her: https://jira.adeo.no/plugins/servlet/desk/portal/541/create/3384. Takk!
                """.trimIndent()
    ),
    UTVANDRET(OppgaveType.UTVANDRET, """
                Brukeren får ikke registrert seg som arbeidssøker fordi bruker står som utvandret i TPS og ikke er befolket i Arena, og har selv opprettet denne oppgaven. 
                
                Ring bruker og følg vanlig rutine for slike tilfeller.
                
                Har oppgaven havnet i feil oppgaveliste? Da ønsker vi som har utviklet løsningen tilbakemelding på dette. Meld sak her: https://jira.adeo.no/plugins/servlet/desk/portal/541/create/3384. Takk!
                """.trimIndent()
    );

    companion object {
        fun from(oppgaveType: OppgaveType): String =
            when (oppgaveType) {
                OppgaveType.OPPHOLDSTILLATELSE -> OPPHOLDSTILLATELSE.tekst
                OppgaveType.UTVANDRET -> UTVANDRET.tekst
            }
    }
}