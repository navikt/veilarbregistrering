package no.nav.fo.veilarbregistrering.oppgave.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OppgaveDto {

    long id;
    String tildeltEnhetsnr;
}
