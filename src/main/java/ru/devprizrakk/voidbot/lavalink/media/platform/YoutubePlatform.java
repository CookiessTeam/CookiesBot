package ru.devprizrakk.voidbot.lavalink.media.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.devprizrakk.voidbot.lavalink.media.DefaultMediaInfo;
import ru.devprizrakk.voidbot.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.lavalink.media.MediaPlatform;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePlatform implements MediaPlatform {

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile("(?:v=|youtu\\.be/)([a-zA-Z0-9_-]{11})");
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final String THUMBNAIL_URL_TEMPLATE = "https://img.youtube.com/vi/%s/hqdefault.jpg";
    private static final String YOUTUBE_API_URL_TEMPLATE = "https://www.googleapis.com/youtube/v3/videos?part=snippet,statistics&id=%s&key=%s";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    @Override
    public String getNamePlatform() {
        return "YouTube";
    }

    @Override
    public boolean supports(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
    }

    @Override
    public MediaInfo resolve(String url) {
        String videoId = extractVideoId(url);
        if (videoId == null) {
            Logger.getLogger().log(LogType.ERROR, "YoutubePlatform", "Invalid YouTube URL: " + url);
            return new DefaultMediaInfo(null, null, null, null, null, null, null);
        }

        JsonObject video = fetchVideoData(videoId);
        String thumbnail = String.format(THUMBNAIL_URL_TEMPLATE, videoId);

        if (video == null) {
            return new DefaultMediaInfo(null, thumbnail, null, null, null, null, null);
        }

        JsonObject stats = video.getAsJsonObject("statistics");
        JsonObject snippet = video.getAsJsonObject("snippet");

        return new DefaultMediaInfo(
                snippet.get("title").getAsString(),
                thumbnail,
                snippet.get("channelTitle").getAsString(),
                stats.get("viewCount").getAsString(),
                stats.get("likeCount").getAsString(),
                stats.get("commentCount").getAsString(),
                snippet.get("publishedAt").getAsString()
        );
    }

    private String extractVideoId(String url) {
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    private JsonObject fetchVideoData(String videoId) {
        HttpResponse<String> response = sendRequest(videoId);
        if (response == null || response.body() == null || response.body().isEmpty()) {
            return null;
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray items = json.getAsJsonArray("items");

        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.get(0).getAsJsonObject();
    }

    private HttpResponse<String> sendRequest(String videoId) {
        String url = String.format(
                YOUTUBE_API_URL_TEMPLATE,
                videoId,
                Utils.getConfig().getString("other.youtube-api.token")
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();

        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch ( IOException e ) {
            Logger.getLogger().log(LogType.ERROR, "YoutubePlatform", "Ошибка запроса для videoId=" + videoId, e);
        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            Logger.getLogger().log(LogType.ERROR, "YoutubePlatform", "Запрос прерван для videoId=" + videoId);
        }

        return null;
    }
}