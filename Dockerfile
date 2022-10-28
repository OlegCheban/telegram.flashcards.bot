FROM adoptopenjdk:11
VOLUME /tmp
ADD target target
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-Dfile.encoding=UTF-8", "-cp", "./target/classes:./target/dependency/*", "ru.flashcards.telegram.BotApplication"]
