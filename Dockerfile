FROM maven
ADD / /source
WORKDIR /source
RUN mvn package -DskipTests

FROM navikt/pus-nais-java-app
COPY /target/veilarbregistrering /app