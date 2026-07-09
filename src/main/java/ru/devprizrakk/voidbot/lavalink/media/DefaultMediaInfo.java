package ru.devprizrakk.voidbot.lavalink.media;

public record DefaultMediaInfo(
        String title,
        String thumbnail,
        String author,
        String viewsCount,
        String likesCount,
        String commentCount,
        String createdDate
) implements MediaInfo {

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getViewsCount() {
        return viewsCount;
    }

    @Override
    public String getLikesCount() {
        return likesCount;
    }

    @Override
    public String getCommentCount() {
        return commentCount;
    }

    @Override
    public String getCreatedDate() {
        return createdDate;
    }
}