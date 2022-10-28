package ru.flashcards.telegram.bot.utils;

import ru.flashcards.telegram.bot.service.SendService;

public class Help {
    public static void sendBotManual(Long chatId){
        SendService.sendMessage(chatId,
                "*Help*\n\n" +
                        "/f to find a new flashcard\n" +
                        "examples:\n" +
                        "/f - suggests flashcards from top3000 category\n" +
                        "/f <word> - finds by a given value\n\n" +
                        "/s to open swiper\n" +
                        "examples:\n" +
                        "/s - displays all flashcards\n" +
                        "/s <letters> - displays flashcards starting with the specified letters\n\n" +
                        "/l to start learning\n\n" +
                        "/exe to enable exercises \n" +
                        "/exd to disable exercises \n\n" +
                        "/i <min> to change notifications interval (default 60 min) \n" +
                        "/edit word#new translation \n" +
                        "/h to display help\n\n"+
                        "Bot sends notifications:\n\n" +
                        "- for spaced repetition. Bot sends only learned flashcards on the 2nd, 3rd, 7th, 14th, 30th and 90th day. At each stage, you can submit flashcard to learn again;\n\n" +
                        "- random flashcards notifications;\n"
        );
    }
}
