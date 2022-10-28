# Flashcards Telegram bot
Bot helps to learn vocabulary words fast and effectively. It uses a spaced repetition system and has a lot of flashcards.

Deployed bot (https://t.me/proFlashcardsBot) has around 100000 flashcards.


####Fast launch with Docker 
- Set environment variables FLASHCARDS_BOT_NAME and FLASHCARDS_BOT_TOKEN in docker-compose.yml file.
- Use command
```
docker compose up -d
```

####Launch in local environment  
```
mvn clean install
mvn liquibase:update

//Windows
java -Xmx300m -Xss512k -Dfile.encoding=UTF-8 -cp ./target/classes;./target/dependency/* ru.flashcards.telegram.BotApplication

//Linux
java -Xmx300m -Xss512k -Dfile.encoding=UTF-8 -cp ./target/classes:./target/dependency/* ru.flashcards.telegram.BotApplication
```

####Help
```
/f to find a new flashcard
examples:
/f - suggests flashcards from top3000 category
/f <word> - finds by a given value

/s to open swiper
examples:
/s - displays all flashcards
/s <letters> - displays flashcards starting with the specified letters

/l to start learning
/exe to enable exercises 
/exd to disable exercises 

/i <min> to change notifications interval (default 60 min) 
/edit word#new translation 
/h to display help

Bot sends notifications:
- for spaced repetition. Bot sends only learned flashcards on the 2nd, 3rd, 7th, 14th, 30th and 90th day. At each stage, you can submit flashcard to learn again;
- random flashcards notifications;
```

####User stories

- I want to be able to swipe flashcards which get in next training.
- I want to be able to pass exercise to check a correct pronounciation.
- I want to be able to pick flashcards for nearest training.
- I want to be able to get flashcards by percentile in swiper.
- I want to be able to set flashcards quantity for training.
 
  
   
