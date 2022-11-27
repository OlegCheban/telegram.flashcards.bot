package ru.flashcards.telegram.bot.utils;

import ru.flashcards.telegram.bot.service.SendService;

public class Help {
    public static void sendBotManual(Long chatId){
        SendService.sendMessage(chatId,
                "*Help*\n\n" +
                        "1. Basic find commands for finding flashcards\n" +
                        "/f - suggests the most popular flashcards\n" +
                        "/f <value> - finds specific flashcard by inputted value\n\n" +

                        "2. Swiper\n" +
                        "Swiper allows you to easily navigate through all of your flashcards\n" +
                        "/s - displays flashcards added by the user to their profile\n" +
                        "/s <value> - displays flashcards containing inputted value\n" +
                        "Additional functionality:\n" +
                        "   - ability to reset learning statistics for flashcards which were already learned (100% progress). Use \"reset progress\" button;\n" +
                        "   - ability to add the most significant flashcards to the next learning session. Use \"boost priority\" button;\n" +
                        "   - ability to see examples of usage. Use \"example of usage\" button.\n\n" +

                        "3. Exercises\n" +
                        "Bot has several kinds of exercises which help you learn flashcards\n" +
                        "/l to start learning\n" +
                        "/e to enable exercises \n" +
                        "/d to disable exercises \n\n" +

                        "4. Watering session (repeat your learned flashcards and pick correct translation before time runs out)\n" +
                        "/w to start watering session\n\n" +

                        "5. Others commands\n" +
                        "/ni <min> - changes random flashcards notifications interval (default 60 min) \n" +
                        "/fq <min> - changes flashcards quantity for training (default 5 flashcards) \n" +
                        "/wt <seconds> - changes watering session reply time (default 5 seconds) \n" +
                        "/h displays help\n\n"+

                        "6. Bot sends notifications:\n" +
                        "   - spaced repetition notifications. Bot sends only learned flashcards on the 2nd, 3rd, 7th, 14th, 30th and 90th day. At each stage, you can reset flashcard to learn again;\n" +
                        "   - random flashcards notifications.\n"
        );
    }
}
