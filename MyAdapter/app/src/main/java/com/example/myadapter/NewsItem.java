package com.example.myadapter;

import java.io.Serializable;
import java.util.Date;

public class NewsItem implements Serializable {
    private final String title;
    private final String shortDescription;
    private final String thumbUrl;
    private final String content;
    private final String largeImageUrl;
    private final Date publicationDate;
    private final int importance;

    public NewsItem(String title, String shortDescription, String thumbUrl, String content, String largeImageUrl, Date publicationDate, int importance) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.thumbUrl = thumbUrl;
        this.content = content;
        this.largeImageUrl = largeImageUrl;
        this.publicationDate = publicationDate;
        this.importance = importance;
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

    public Date getPublicationDate() {
        return publicationDate;
    }

    public int getImportance() {
        return importance;
    }
}