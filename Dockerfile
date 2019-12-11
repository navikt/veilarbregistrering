FROM navikt/pus-nais-java-app

RUN mvn package -DskipTests

COPY /target/veilarboppfolging /app