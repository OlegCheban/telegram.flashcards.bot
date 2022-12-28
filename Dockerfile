#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim as build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM adoptopenjdk:11
COPY --from=build /home/app/target/classes /usr/local/lib/app/classes
COPY --from=build /home/app/target/dependency /usr/local/lib/app/dependency
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-Dfile.encoding=UTF-8", "-cp", "./usr/local/lib/app/classes:./usr/local/lib/app/dependency/*", "ru.flashcards.telegram.BotApplication"]
