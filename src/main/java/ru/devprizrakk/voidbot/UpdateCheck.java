package ru.devprizrakk.voidbot;

import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateCheck  {
    private static String getLatestReleaseVersion(String owner, String repo) throws IOException {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            Logger.getLogger().log(LogType.ERROR,"github", "Failed to get the latest release version. HTTP response code: " + responseCode);
            return null;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // Пример простого извлечения версии из JSON ответа
            String response = content.toString();
            int tagIndex = response.indexOf("\"tag_name\":\"");
            if (tagIndex != -1) {
                int startIndex = tagIndex + 12;
                int endIndex = response.indexOf("\"", startIndex);
                return response.substring(startIndex, endIndex);
            } else {
                Logger.getLogger().log(LogType.ERROR,"github", "Failed to parse the latest release version from GitHub response.");
                return null;
            }
        }
    }

}
