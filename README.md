# Veilarbregistrering

Backend-applikasjon for [Arbeidssøkerregistrering](https://github.com/navikt/arbeidssokerregistrering) som tar i mot nyregistrering av arbeidssøkere.

![](https://github.com/navikt/veilarbregistrering/workflows/Build,%20push,%20deploy%20%F0%9F%92%AA/badge.svg)

## API
Se https://veilarbregistrering.nais.adeo.no/veilarbregistrering/internal/swagger/index.html?input_baseurl=/veilarbregistrering/api/swagger.json 
for beskrivelse av APIet til `veilarbregistrering`.

## Avhengigheter
- veilarboppfolging (og Arena) : REST
- [Arena ORDS : REST](src/main/java/no/nav/fo/veilarbregistrering/arbeidssoker/adapter/README.md)
- veilarbperson : REST
- Aktør-service (fnr/dnr -> aktørId) : SOAP
- ABAC (tilgangskontroll)
- Aareg (siste arbeidsforhold) : SOAP
- [Enhetsregisteret](src/main/java/no/nav/fo/veilarbregistrering/enhet/adapter/README.md)
- [NAV Organisasjon (for veileder pr ident)](src/main/java/no/nav/fo/veilarbregistrering/orgenhet/adapter/README.md)
- Infotrygd (maksdato) : REST
- Unleash (feature toggle)
- Oppgave : REST -> "kontakt bruker"-oppgaver
- PDL : Graphql
- [KRR : REST](src/main/java/no/nav/fo/veilarbregistrering/bruker/krr/README.md)

# Komme i gang

```
# bygge
mvn clean install 

# test
mvn test

# starte
# Kjør main-metoden i Main.java
# For lokal test kjøring kjør MainTest.java
```

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles via issues her på github.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen `#område-arbeid-pilo`t.
