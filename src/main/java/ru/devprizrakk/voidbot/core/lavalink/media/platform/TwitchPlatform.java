package ru.devprizrakk.voidbot.core.lavalink.media.platform;

import ru.devprizrakk.voidbot.core.lavalink.media.DefaultMediaInfo;
import ru.devprizrakk.voidbot.core.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.core.lavalink.media.MediaPlatform;

public class TwitchPlatform implements MediaPlatform {

    @Override
    public String getNamePlatform() {
        return "Twitch";
    }

    @Override
    public boolean supports(String url) {
        return url.contains("twitch.tv");
    }

    @Override
    public MediaInfo resolve(String url) {

        // Для Twitch обычно нужна работа с API,
        // здесь пока просто заглушка

        return new DefaultMediaInfo(
                "Unknown Twitch Stream",
                null,
                "Unknown streamer",
                "null",
                "null",
                "null",
                "null"
        );
    }
}