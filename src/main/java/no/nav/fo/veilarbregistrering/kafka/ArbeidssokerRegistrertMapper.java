package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertInternalEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static java.util.Optional.ofNullable;

class ArbeidssokerRegistrertMapper {

    static ArbeidssokerRegistrertEvent map(ArbeidssokerRegistrertInternalEvent event) {
        return ArbeidssokerRegistrertEvent.newBuilder()
                .setAktorid(event.getAktorId().asString())
                .setBrukersSituasjon(ofNullable(event.getBrukersSituasjon())
                        .map(Enum::toString).orElse(null))
                .setRegistreringOpprettet(
                        ZonedDateTime.of(
                                event.getOpprettetDato(),
                                ZoneId.systemDefault()).format(ISO_ZONED_DATE_TIME))
                .build();
    }
}
