package com.openwiki.controller;

import io.javalin.http.Context;
import com.openwiki.service.WikiService;
import com.openwiki.dao.ArticleDAO;
import com.openwiki.model.Article;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class WikiController {
    private final WikiService wikiService;
    private final ArticleDAO articleDAO;

    public WikiController() {
        this.wikiService = new WikiService();
        this.articleDAO = new ArticleDAO();
    }

    public void search(Context ctx) {
        try {
            String query = ctx.queryParam("query");
            int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
            
            if (query == null || query.trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "Query parameter is required"));
                return;
            }

            ctx.json(wikiService.search(query, limit));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    public void getArticle(Context ctx) {
        try {
            String title = ctx.pathParam("title");
            Article article = wikiService.getArticle(title);
            
            ctx.json(article);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to get article: " + e.getMessage()));
        }
    }

    public void saveArticle(Context ctx) {
        try {
            Article article = ctx.bodyAsClass(Article.class);
            Article savedArticle = articleDAO.save(article);
            ctx.json(savedArticle);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to save article: " + e.getMessage()));
        }
    }

    public void getUserArticles(Context ctx) {
        try {
            String userId = ctx.queryParam("userId");
            if (userId == null || userId.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "userId parameter is required");
                ctx.status(400).json(error);
                return;
            }

            List<Article> articles = articleDAO.findByUserId(userId);
            ctx.json(articles);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user articles: " + e.getMessage());
            ctx.status(500).json(error);
        }
    }

    public void getFeaturedArticle(Context ctx) {
        try {
            Article article = wikiService.getFeaturedArticle();
            ctx.json(article);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to get featured article: " + e.getMessage()));
        }
    }
} 