package ru.devprizrakk.voidbot.commands.music.lavalink.media;

public interface MediaPlatform {

    String getNamePlatform();
    boolean supports(String url);
    MediaInfo resolve(String url);
}