package ru.devprizrakk.voidbot.lavalink.media.platform;

import ru.devprizrakk.voidbot.lavalink.media.DefaultMediaInfo;
import ru.devprizrakk.voidbot.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.lavalink.media.MediaPlatform;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpRadioPlatform implements MediaPlatform {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    @Override
    public String getNamePlatform() {
        return "Http Radio (mp3 stream)";
    }

    @Override
    public boolean supports(String url) {
        String contentType = fetchContentType(url);
        return isAudioContentType(contentType);
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

    private String fetchContentType(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.headers()
                    .firstValue("Content-Type")
                    .orElse("")
                    .toLowerCase();
        } catch ( IOException e ) {
            Logger.getLogger().log(LogType.ERROR, "HttpRadioPlatform", "Ошибка запроса к " + url + ": " + e.getMessage());
        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            Logger.getLogger().log(LogType.ERROR, "HttpRadioPlatform", "Запрос прерван для " + url);
        }

        return "";
    }

    private boolean isAudioContentType(String contentType) {
        return contentType.startsWith("audio/")
                || contentType.contains("mpegurl")
                || contentType.contains("ogg");
    }
}
