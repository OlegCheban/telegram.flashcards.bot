package ru.flashcards.telegram.bot.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class SendService {
    private static final String telegramUrl = "https://api.telegram.org";

    public static void sendPhoto(Long chatId, String fileId) {
        StringBuilder sb = new StringBuilder();
        sb
          .append(telegramUrl)
          .append("/bot").append(System.getenv().get("FLASHCARDS_BOT_TOKEN"))
          .append("/sendPhoto?chat_id=").append(chatId).append("&photo=").append(fileId);

        postRequest(sb.toString());
    }

    public static void sendMessage(Long chatId, String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());

            StringBuilder sb = new StringBuilder();
            sb
              .append(telegramUrl)
              .append("/bot").append(System.getenv().get("FLASHCARDS_BOT_TOKEN"))
              .append("/sendMessage?chat_id=").append(chatId).append("&parse_mode=Markdown").append("&text=").append(encodedText);

            postRequest(sb.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(Long chatId, String text, String replyMarkup) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
            String encodedReplyMarkup = URLEncoder.encode(replyMarkup, StandardCharsets.UTF_8.toString());

            StringBuilder sb = new StringBuilder();
            sb
                    .append(telegramUrl)
                    .append("/bot").append(System.getenv().get("FLASHCARDS_BOT_TOKEN"))
                    .append("/sendMessage?chat_id=").append(chatId).append("&parse_mode=Markdown").append("&text=").append(encodedText).append("&reply_markup=").append(encodedReplyMarkup);

            postRequest(sb.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void postRequest(String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            if (connection.getResponseCode() != 200) {
                System.err.println(String.format("Request failed: %s", uri));
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
