package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.util.List;

import static java.util.stream.Collectors.toList;

class FormidlingshistorikkMapper {

    static List<Arbeidssokerperiode> map(FormidlingsgruppeResponseDto response) {
        return response.getFormidlingshistorikk()
                .stream()
                .map(r -> map(r))
                .collect(toList());
    }

    private static Arbeidssokerperiode map(FormidlingshistorikkDto formidlingshistorikkDto) {
        return new Arbeidssokerperiode(
                Formidlingsgruppe.of(formidlingshistorikkDto.getFormidlingsgruppeKode()),
                Periode.of(
                        formidlingshistorikkDto.getFraDato(),
                        formidlingshistorikkDto.getTilDato()));
    }
}
