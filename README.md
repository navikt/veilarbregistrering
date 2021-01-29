# Veilarbregistrering

Backend-applikasjon for [Arbeidssøkerregistrering](https://github.com/navikt/arbeidssokerregistrering) som tar i mot nyregistrering av arbeidssøkere.

![](https://github.com/navikt/veilarbregistrering/workflows/Build,%20push,%20deploy%20%F0%9F%92%AA/badge.svg)

## API
Se https://veilarbregistrering.nais.adeo.no/veilarbregistrering/internal/swagger/index.html?input_baseurl=/veilarbregistrering/api/swagger.json 
for beskrivelse av APIet til `veilarbregistrering`.

## Innkommende kommuniksjon (inbound communication)
| Collaborator | Query/Command/Event | Melding |
| --- | --- | --- |
| Arbeidssokerregistrering | query (REST/GET) | /startregistrering |
| Arbeidssokerregistrering | command (REST/POST) | /startregistrering |
| Arbeidssokerregistrering | query (REST/GET) | /registrering |
| PTO | query (REST/GET) | /registrering |
| Arbeidssokerregistrering | command (REST/POST) | /startreaktivering |
| Arbeidssokerregistrering | command (REST/POST) | /startregistrersykmeldt |
| Arbeidssokerregistrering | query (REST/GET) | /sistearbeidsforhold |
| Arbeidssokerregistrering | query (REST/GET) | /person/kontaktinfo |
| Arbeidssokerregistrering | command (REST/POST) | /oppgave |
| Dagpenger | query (REST/GET) | /arbeidssoker/perioder |
| Arena | event (Kafka) | FormidlingsgruppeEvent |
| Helse | query (REST/GET) | /sykmeldtinfodata |

## Utgående kommunikasjon (outbound communication)
| Melding | Query/Command/Event | Collaborator |
| :--- | :--- | :--- |
| [Arena ORDS](src/main/java/no/nav/fo/veilarbregistrering/arbeidssoker/adapter/README.md) | query (REST/GET) | Arena |
| --- | Command (REST/POST) | veilarboppfolging (og Arena) |
| --- | Query (REST/GET) | veilarboppfolging (og Arena) |
| --- | Query (REST/GET) | veilarbperson |
| --- | --- | ABAC (tilgangskontroll) |
| --- | Query (REST/GET) | [Aareg (siste arbeidsforhold)](src/main/java/no/nav/fo/veilarbregistrering/arbeidsforhold/adapter/README.md) |
| --- | Query (REST/GET) | [Enhetsregisteret](src/main/java/no/nav/fo/veilarbregistrering/enhet/adapter/README.md) |
| --- | --- | [NAV Organisasjon (for veileder pr ident)](src/main/java/no/nav/fo/veilarbregistrering/orgenhet/adapter/README.md) |
| maksdato | Query (REST/GET) | Infotrygd |
| --- | --- | Unleash (feature toggle) |
| "kontakt bruker"-oppgave | --- | [Oppgave](src/main/java/no/nav/fo/veilarbregistrering/oppgave/adapter/README.md) |
| --- | Query (Graphql) | [PDL](src/main/java/no/nav/fo/veilarbregistrering/bruker/pdl/README.md) |
| --- | Query (REST/GET) | [KRR](src/main/java/no/nav/fo/veilarbregistrering/bruker/krr/README.md) |

# Komme i gang

```
# bygge
mvn clean install 

# test
mvn test

# starte
# Kjør main-metoden i Main.java
# For lokal test kjøring kjør ApplicationLocal.java
```

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles via issues her på github.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen `#område-arbeid-paw`.
