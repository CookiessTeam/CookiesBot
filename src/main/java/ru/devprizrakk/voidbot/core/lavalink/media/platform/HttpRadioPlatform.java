package ru.devprizrakk.voidbot.core.lavalink.media.platform;

import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.core.lavalink.media.DefaultMediaInfo;
import ru.devprizrakk.voidbot.core.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.core.lavalink.media.MediaPlatform;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpRadioPlatform implements MediaPlatform {
    @Override
    public String getNamePlatform() {
        return "Http Radio (mp3 stream)";
    }

    @Override
    public boolean supports(String url) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response =
                null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            Logger.getLogger().log(LogType.ERROR, "HttpRadioPlatform", "Неизвестная ошибка" + url);
        }

        assert response != null;
        String contentType = response.headers()
                .firstValue("Content-Type")
                .orElse("")
                .toLowerCase();

        System.out.println("Content-Type: " + contentType);

        return contentType.startsWith("audio/")
                || contentType.contains("mpegurl")
                || contentType.contains("ogg");
    }

    @Override
    public MediaInfo resolve(String url) {
        return new DefaultMediaInfo(
                "Audio Live Steam",
                null,
                "lavalink-audio-thread",
                null,
                null,
                null,
                null
    );
    }
}
