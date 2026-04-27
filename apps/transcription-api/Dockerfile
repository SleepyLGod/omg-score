FROM maven:3.9.9-eclipse-temurin-17

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ffmpeg \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace/apps/transcription-api

CMD ["sh", "-c", "mvn -pl pianotranscriptioncli -am -DskipTests install && mvn -pl pianotranscriptioncli spring-boot:run"]
