/*
 * Copyright (c) 2022 FORS Development Center
 * Trifonovskiy tup. 3, Moscow, 129272, Russian Federation
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * FORS Development Center ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with FORS.
 */

package ru.flashcards.telegram.bot.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileApi {
    public static final String FILE_API = "https://api.telegram.org/file/bot";

    private final String apiUrl;

    public FileApi(String token) {
        this(FILE_API, token);
    }

    public FileApi(String apiUrl, String token) {
        this.apiUrl = apiUrl + token + "/";
    }

    public String getFullFilePath(String filePath) {
        int slash = filePath.lastIndexOf('/') + 1;
        String path = filePath.substring(0, slash);
        String fileName = filePath.substring(slash);
        try {
            return apiUrl + path + URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return apiUrl + filePath;
        }
    }
}
