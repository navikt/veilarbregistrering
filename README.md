# Veilarbregistrering

Backend-applikasjon for [Arbeidssøkerregistrering](https://github.com/navikt/arbeidssokerregistrering) som tar i mot nyregistrering av arbeidssøkere.

![](https://github.com/navikt/veilarbregistrering/workflows/Build,%20push,%20and%20deploy/badge.svg)

## Avhengigheter
- veilarboppfolging (og Arena) : REST
- veilarbperson : REST
- Aktør-service (fnr/dnr -> aktørId) : SOAP
- ABAC (tilgangskontroll)
- Aareg (siste arbeidsforhold)
- NAV Enhet (for veileder pr ident)
- Infotrygd (maksdato) : REST
- Unleash (feature toggle)
- Oppgave : REST
- PDL

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
