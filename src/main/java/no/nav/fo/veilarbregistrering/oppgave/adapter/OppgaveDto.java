package no.nav.fo.veilarbregistrering.oppgave.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class OppgaveDto {

    private String aktoerId;
    private String beskrivelse;
    private String tema;
    private String oppgavetype;
    private String fristFerdigstillelse;
    private String aktivDato;
    private String prioritet;

}
