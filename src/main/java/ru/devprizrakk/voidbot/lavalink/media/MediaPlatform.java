package ru.devprizrakk.voidbot.lavalink.media;

public interface MediaPlatform {
    String getNamePlatform();

    boolean supports(String url);

    MediaInfo resolve(String url);
}