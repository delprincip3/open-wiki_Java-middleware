package com.openwiki.controller;

import io.javalin.http.Context;
import com.openwiki.service.WikiService;
import com.openwiki.dao.ArticleDAO;
import com.openwiki.model.Article;
import java.util.HashMap;
import java.util.Map;

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
                Map<String, String> error = new HashMap<>();
                error.put("error", "Query parameter is required");
                ctx.status(400).json(error);
                return;
            }

            ctx.json(wikiService.search(query, limit));
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed: " + e.getMessage());
            ctx.status(500).json(error);
        }
    }

    public void getArticle(Context ctx) {
        try {
            String title = ctx.pathParam("title");
            ctx.json(wikiService.getArticle(title));
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get article: " + e.getMessage());
            ctx.status(500).json(error);
        }
    }

    public void saveArticle(Context ctx) {
        try {
            Article article = ctx.bodyAsClass(Article.class);
            Article savedArticle = articleDAO.save(article);
            ctx.json(savedArticle);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to save article: " + e.getMessage());
            ctx.status(500).json(error);
        }
    }
} 