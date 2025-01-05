package com.openwiki.model;

public class WikiSearchResult {
    private String title;
    private String excerpt;
    private String pageId;
    private String url;

    // Getters
    public String getTitle() { return title; }
    public String getExcerpt() { return excerpt; }
    public String getPageId() { return pageId; }
    public String getUrl() { return url; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    public void setUrl(String url) { this.url = url; }
} 