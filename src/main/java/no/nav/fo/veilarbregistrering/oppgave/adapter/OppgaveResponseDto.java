package no.nav.fo.veilarbregistrering.oppgave.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.fo.veilarbregistrering.oppgave.Oppgave;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class OppgaveResponseDto implements Oppgave {

    long id;
    String tildeltEnhetsnr;
}
