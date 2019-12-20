# Veilarbregistrering

Applikasjon for nyregistrering av arbeidssøkere.

## Avhengigheter
- veilarboppfolging (og Arena)
- veilarbperson
- Aktør-service (fnr/dnr -> aktørId)
- ABAC (tilgangskontroll)
- Aareg (siste arbeidsforhold)
- NAV Enhet (for veileder pr ident)
- Infotrygd (maksdato)

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

Interne henvendelser kan sendes via Slack i kanalen #område-arbeid-pilot.
