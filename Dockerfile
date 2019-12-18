FROM maven:3-jdk-8 as builderContainer
ADD / /source
WORKDIR /source
RUN mvn package -DskipTests

FROM navikt/pus-nais-java-app
COPY --from=builderContainer /source/target/veilarbregistrering /app