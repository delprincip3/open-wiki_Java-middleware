package com.openwiki.model;

import java.time.LocalDateTime;

public class Article {
    private String id;           // Sarà convertito da/a INT
    private String userId;       // Sarà convertito da/a INT
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime dateDownloaded;
    
    // Campi temporanei per l'integrazione con Wikipedia
    private transient String pageId;    
    private transient String wikiUrl;   

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public LocalDateTime getDateDownloaded() { return dateDownloaded; }
    public void setDateDownloaded(LocalDateTime dateDownloaded) { this.dateDownloaded = dateDownloaded; }
    
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    
    public String getWikiUrl() { return wikiUrl; }
    public void setWikiUrl(String wikiUrl) { this.wikiUrl = wikiUrl; }
} 