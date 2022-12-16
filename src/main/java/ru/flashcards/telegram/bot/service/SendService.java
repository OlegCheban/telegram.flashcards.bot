package ru.flashcards.telegram.bot.service;

import org.apache.commons.io.FileUtils;

import java.io.*;
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

    public static File getFile (String path) throws IOException {
        try {
            URL url = new URL(new FileApi(System.getenv().get("FLASHCARDS_BOT_TOKEN")).getFullFilePath(path));
            File f = new File("audio.ogg");
            FileUtils.writeByteArrayToFile(f, download(url));
            return f;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] download(URL url) throws IOException {
        URLConnection uc = url.openConnection();
        int len = uc.getContentLength();
        InputStream is = new BufferedInputStream(uc.getInputStream());
        try {
            byte[] data = new byte[len];
            int offset = 0;
            while (offset < len) {
                int read = is.read(data, offset, data.length - offset);
                if (read < 0) {
                    break;
                }
                offset += read;
            }
            if (offset < len) {
                throw new IOException(
                        String.format("Read %d bytes; expected %d", offset, len));
            }
            return data;
        } finally {
            is.close();
        }
    }
}
