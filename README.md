# Flashcards Telegram bot
Bot helps to learn vocabulary words fast and effectively. It uses spaced repetition learning technique and has a lot of flashcards.

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
/f - suggests the most popular flashcards
/f <value> - finds specific flashcard by inputted value

2. Swiper
Swiper allows you to easily navigate through all of your flashcards
/s - displays flashcards added by the user to their profile
/s <value> - displays flashcards containing inputted value
Additional functionality:
   - ability to reset learning statistics for flashcards which were already learned (100% progress). Use "reset progress" button;
   - ability to add the most significant flashcards to the next learning session. Use "boost priority" button;
   - ability to see examples of usage. Use "example of usage" button.

3. Exercises
Bot has several kinds of exercises which help you learn flashcards
/l to start learning
/e to enable exercises 
/d to disable exercises

4. Watering session (repeat your learned flashcards and pick correct translation before time runs out)
/w to start watering session

5. Others commands
/ni <min> - changes random flashcards notifications interval (default 60 min) 
/fq <min> - changes flashcards quantity for training (default 5 flashcards)
/wt <seconds> - changes watering session reply time (default 5 seconds)
/ed <value> - finds flashcard from your profile by inputted value (english word) and suggests to change translation
/h displays help

6. Bot sends notifications:
   - spaced repetition notifications. Bot sends only learned flashcards on the 2nd, 3rd, 7th, 14th, 30th and 90th day. At each stage, you can reset flashcard to learn again;
   - random flashcards notifications.
```

#### User stories
  
- I want to be able to pass exercise to check a correct pronounciation.
- I want to be able to get flashcards by percentile in swiper.
- I want to be able to get similar flashcards when one of mine have not been found due to misspelling.  

 
  
   
