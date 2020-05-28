package no.nav.fo.veilarbregistrering.oppgave.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class OppgaveResponseResponseDto implements OppgaveResponse {

    long id;
    String tildeltEnhetsnr;
}
