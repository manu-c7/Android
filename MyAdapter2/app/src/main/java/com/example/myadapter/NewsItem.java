package com.example.myadapter;

import java.io.Serializable;

public class NewsItem implements Serializable {
    private final String title;
    private final String shortDescription;
    private final String thumbUrl;
    private final String content;
    private final String largeImageUrl;

    public NewsItem(String title, String shortDescription, String thumbUrl, String content, String largeImageUrl) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.thumbUrl = thumbUrl;
        this.content = content;
        this.largeImageUrl = largeImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getContent() {
        return content;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }
}