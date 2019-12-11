FROM maven
RUN mvn package -DskipTests

FROM navikt/pus-nais-java-app
COPY /target/veilarboppfolging /app