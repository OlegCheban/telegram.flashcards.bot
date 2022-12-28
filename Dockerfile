#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim as build
COPY . /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM adoptopenjdk:11
COPY --from=build /home/app/target /usr/local/lib/app
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-Dfile.encoding=UTF-8", "-cp", "./usr/local/lib/app/classes:./usr/local/lib/app/dependency/*", "ru.flashcards.telegram.BotApplication"]
