package ru.flashcards.telegram.bot.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.flashcards.telegram.bot.botapi.swiper.Swiper;
import ru.flashcards.telegram.bot.db.dmlOps.SwiperDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;
import ru.flashcards.telegram.bot.service.SendService;

public class SwiperCommand extends BotCommand {
    private final String badParameters = "Bad parameters";
    private final String flashcardsNotFound = "Flashcards not found";
    private SwiperDataHandler swiperDataHandler;

    public SwiperCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
        this.swiperDataHandler = new SwiperDataHandler();
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        super.processMessage(absSender, message, arguments);

        String characterConditionParam = "";
        if (arguments.length > 1) {
            SendService.sendMessage(message.getChatId(), badParameters);
            return;
        }

        if (arguments.length == 1){
            characterConditionParam = arguments[0];
        }

        Long firstFlashcard = swiperDataHandler.getFirstSwiperFlashcard(message.getChatId(), characterConditionParam);

        if (firstFlashcard != null){
            SwiperFlashcard swiperFlashcard = swiperDataHandler.getSwiperFlashcard(message.getChatId(), firstFlashcard, characterConditionParam);
            if (swiperFlashcard != null){
                Swiper swiper = new Swiper(characterConditionParam, swiperFlashcard.getPrevId(), swiperFlashcard.getNextId(), swiperFlashcard.getCurrentId(), swiperFlashcard.getLearnPrc());

                SendMessage replyMessage = new SendMessage();
                replyMessage.setChatId(String.valueOf(message.getChatId()));
                replyMessage.setText("*" + swiperFlashcard.getWord() + "* \\[" + swiperFlashcard.getTranscription() + "] ("+swiperFlashcard.getLearnPrc()+"% learned)\n" + swiperFlashcard.getDescription() + "\n\n" + "*Translation:* " + swiperFlashcard.getTranslation());
                replyMessage.enableMarkdown(true);
                replyMessage.setReplyMarkup(swiper.getSwiperMarkup());
                try {
                    absSender.execute(replyMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                SendService.sendMessage(message.getChatId(), flashcardsNotFound);
            }


        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
    }
}
