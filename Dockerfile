FROM maven:3-jdk-8
ADD / /source
WORKDIR /source
RUN mvn package -DskipTests

FROM navikt/pus-nais-java-app
COPY /source/target/veilarbregistrering /app