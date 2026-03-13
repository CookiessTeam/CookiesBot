package ru.devprizrakk.voidbot.core.lavalink.media;

public interface MediaPlatform {

    String getNamePlatform();
    boolean supports(String url);
    MediaInfo resolve(String url);
}