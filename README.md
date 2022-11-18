# Flashcards Telegram bot
Bot helps to learn vocabulary words fast and effectively. It uses a spaced repetition system and has a lot of flashcards.

Deployed bot (https://t.me/proFlashcardsBot) has around 100000 flashcards.


#### Fast launch with Docker 
- Set environment variables FLASHCARDS_BOT_NAME and FLASHCARDS_BOT_TOKEN in docker-compose.yml file.
- Use command
```
mvn clean install
docker compose up -d
```

#### Launch in local environment  
```
mvn clean install
mvn liquibase:update

//Windows
java -Xmx300m -Xss512k -Dfile.encoding=UTF-8 -cp ./target/classes;./target/dependency/* ru.flashcards.telegram.BotApplication

//Linux
java -Xmx300m -Xss512k -Dfile.encoding=UTF-8 -cp ./target/classes:./target/dependency/* ru.flashcards.telegram.BotApplication
```

#### Help
```
Help

1. Basic find commands for finding flashcards
/f - finds a new flashcard (by default bot suggests flashcards from top 3000 category)
/f <value> - finds flashcards which start from an input value

2. Swiper
With swiper you can see your flashcards
/s - displays all flashcards
/s <value> - displays flashcards which start from an input value
Additional functionality:
   - ability to reset statistics for flashcards which have already learned (100% progress). Use "reset progress" button;
   - ability to pick flashcards for nearest training. Use "boost priority" button.
   - ability to see examples of usage. Use "example of usage" button.

3. Exercises
Bot has several kinds of drills which help you learn flashcards
/l to start learning
/ee to enable exercises 
/de to disable exercises 

4. Others commands
/ni <min> - changes notifications interval (default 60 min) 
/fq <min> - changes flashcards quantity for training (default 5 flashcards)
/h displays help

5. Bot sends notifications:
   - for spaced repetition. Bot sends only learned flashcards on the 2nd, 3rd, 7th, 14th, 30th and 90th day. At each stage, you can submit flashcard to learn again;
   - random flashcards notifications.
```

#### User stories

- I want to be able to get a feature like watering sessions in the Memrise.  
- I want to be able to pass exercise to check a correct pronounciation.
- I want to be able to get flashcards by percentile in swiper.
- I want to be able to edit translation for my flashcards.
- I want to be able to get similar flashcards when one of mine have not been found by misspell reason.  

 
  
   
