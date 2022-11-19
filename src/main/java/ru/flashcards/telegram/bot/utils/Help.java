package ru.flashcards.telegram.bot.utils;

import ru.flashcards.telegram.bot.service.SendService;

public class Help {
    public static void sendBotManual(Long chatId){
        SendService.sendMessage(chatId,
                "*Help*\n\n" +
                        "1. Basic find commands for finding flashcards\n" +
                        "/f - finds a new flashcard (by default bot suggests flashcards from top 3000 category)\n" +
                        "/f <value> - finds flashcards which start from an input value\n\n" +

                        "2. Swiper\n" +
                        "With swiper you can see your flashcards\n" +
                        "/s - displays all flashcards\n" +
                        "/s <value> - displays flashcards which start from an input value\n" +
                        "Additional functionality:\n" +
                        "   - ability to reset statistics for flashcards which have already learned (100% progress). Use \"reset progress\" button.\n" +
                        "   - ability to pick flashcards for nearest training. Use \"boost priority\" button.\n" +
                        "   - ability to see examples of usage. Use \"example of usage\" button.\n\n" +

                        "3. Exercises\n" +
                        "Bot has several kinds of drills which help you learn flashcards\n" +
                        "/l to start learning\n" +
                        "/ee to enable exercises \n" +
                        "/de to disable exercises \n\n" +

                        "4. Watering session (repeat your learned flashcards and pick correct translation till time out)\n" +
                        "/ws to start watering session\n\n" +

                        "5. Others commands\n" +
                        "/ni <min> - changes notifications interval (default 60 min) \n" +
                        "/fq <min> - changes flashcards quantity for training (default 5 flashcards) \n" +
                        "/wt <seconds> - changes watering session reply time (default 5 seconds) \n" +
                        "/h displays help\n\n"+

                        "6. Bot sends notifications:\n" +
                        "   - for spaced repetition. Bot sends only learned flashcards on the 2nd, 3rd, 7th, 14th, 30th and 90th day. At each stage, you can submit flashcard to learn again;\n" +
                        "   - random flashcards notifications;\n"
        );
    }
}
