package ru.devprizrakk.voidbot.module.music.lavalink.media.platform;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.devprizrakk.voidbot.api.logging.LogType;
import ru.devprizrakk.voidbot.api.logging.Logger;
import ru.devprizrakk.voidbot.api.utils.Utils;
import ru.devprizrakk.voidbot.module.music.lavalink.media.DefaultMediaInfo;
import ru.devprizrakk.voidbot.module.music.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.module.music.lavalink.media.MediaPlatform;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePlatform implements MediaPlatform {

    private static final Pattern YOUTUBE_PATTERN =
            Pattern.compile("(?:v=|youtu\\.be/)([a-zA-Z0-9_-]{11})");

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
        String likeCount;
        String viewCount;
        String commentCount;
        String dateCreated;
        String title;
        String author;

        if (!(getResponse(videoId) == null || Objects.requireNonNull(getResponse(videoId)).body().isEmpty() || Objects.requireNonNull(getResponse(videoId)).body() == null)) {
            JsonObject json = JsonParser.parseString(Objects.requireNonNull(getResponse(videoId)).body()).getAsJsonObject();
            JsonArray items = json.getAsJsonArray("items");

            if (items != null && !items.isEmpty()) {
                JsonObject video = items.get(0).getAsJsonObject();
                JsonObject stats = video.getAsJsonObject("statistics");

                likeCount = stats.get("likeCount").getAsString();
                viewCount = stats.get("viewCount").getAsString();
                commentCount = stats.get("commentCount").getAsString();

                JsonObject snippet = video.getAsJsonObject("snippet");
                dateCreated = snippet.get("publishedAt").getAsString();
                title = snippet.get("title").getAsString();
                author = snippet.get("channelTitle").getAsString();
            } else {
                likeCount = null;
                viewCount = null;
                commentCount = null;
                dateCreated = null;
                author = null;
                title = null;
            }
        } else {
            likeCount = null;
            viewCount = null;
            commentCount = null;
            dateCreated = null;
            author = null;
            title = null;
        }

        if (videoId == null) {
            Logger.getLogger().log(LogType.ERROR, "YoutubePlatform", "Invalid YouTube URL: " + url);
        }

        String thumbnail = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";

        return new DefaultMediaInfo(
                title,
                thumbnail,
                author,
                viewCount,
                likeCount,
                commentCount,
                dateCreated
        );
    }

    private String extractVideoId(String url) {
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }
    private HttpResponse<String> getResponse(String videoID) {

        String url = String.format(
                "https://www.googleapis.com/youtube/v3/videos?part=snippet,statistics&id=%s&key=%s",
                videoID,
                Utils.getConfigManager().getConfig().getString("other.youtube-api.token")
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Logger.getLogger().log(LogType.ERROR, "YoutubePlatform", "Неизвестная ошибка", e);
            return null;
        }
    }
}