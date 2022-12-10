package ru.flashcards.telegram.bot.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.flashcards.telegram.bot.botapi.swiper.Swiper;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;
import ru.flashcards.telegram.bot.service.SendService;
import ru.flashcards.telegram.bot.utils.Number;

import static ru.flashcards.telegram.bot.botapi.Literals.FLASHCARDS_NOT_FOUND_MSG;
import static ru.flashcards.telegram.bot.botapi.Literals.UNRECOGNIZED_OPTION_MSG;

public class SwiperCommand extends BotCommand {
    private DataLayerObject dataLayer;

    public SwiperCommand(String commandIdentifier, String description, DataLayerObject dataLayerObject) {
        super(commandIdentifier, description);
        this.dataLayer = dataLayerObject;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        String characterConditionParam = "";
        String percentile = "";

        if (arguments.length > 2) {
            SendService.sendMessage(message.getChatId(), UNRECOGNIZED_OPTION_MSG);
            return;
        }

        if (arguments.length == 1) {
            if (Number.isInteger(arguments[0], 10)) {
                percentile = arguments[0];
            } else {
                characterConditionParam = arguments[0];
            }
        }

        if (arguments.length == 2) {
            boolean firstIsNumber = Number.isInteger(arguments[0], 10);
            boolean secondIsNumber = Number.isInteger(arguments[1], 10);
            if (firstIsNumber && secondIsNumber || !firstIsNumber && !secondIsNumber){
                SendService.sendMessage(message.getChatId(), UNRECOGNIZED_OPTION_MSG);
                return;
            }
            percentile = firstIsNumber ? arguments[0] : arguments[1];
            characterConditionParam = !firstIsNumber ? arguments[0] : arguments[1];
        }

        Long firstFlashcard = dataLayer.getFirstSwiperFlashcard(message.getChatId(), characterConditionParam, percentile);

        if (firstFlashcard != null){
            SwiperFlashcard swiperFlashcard =
                    dataLayer.getSwiperFlashcard(message.getChatId(), firstFlashcard, characterConditionParam, percentile);
            if (swiperFlashcard != null){
                Swiper swiper = new Swiper(characterConditionParam, swiperFlashcard, percentile);

                SendMessage replyMessage = new SendMessage();
                replyMessage.setChatId(String.valueOf(message.getChatId()));
                replyMessage.setText("*" + swiperFlashcard.getWord() + "* \\[" + swiperFlashcard.getTranscription() +
                        "] ("+swiperFlashcard.getLearnPrc()+"% learned)\n" + swiperFlashcard.getDescription() +
                        "\n\n" + "*Translation:* " + swiperFlashcard.getTranslation());
                replyMessage.enableMarkdown(true);
                replyMessage.setReplyMarkup(swiper.getSwiperKeyboardMarkup());
                try {
                    absSender.execute(replyMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                SendService.sendMessage(message.getChatId(), FLASHCARDS_NOT_FOUND_MSG);
            }
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
    }
}
