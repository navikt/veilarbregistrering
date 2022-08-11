FROM ghcr.io/navikt/pus-nais-java-app/pus-nais-java-app:java17
COPY /target/veilarbregistrering.jar app.jar
