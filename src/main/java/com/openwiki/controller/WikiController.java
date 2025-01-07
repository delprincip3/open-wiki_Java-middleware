package com.openwiki.controller;

import io.javalin.http.Context;
import com.openwiki.service.WikiService;
import com.openwiki.dao.ArticleDAO;
import com.openwiki.model.Article;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;

public class WikiController {
    private final WikiService wikiService;
    private final ArticleDAO articleDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(WikiController.class);

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
            
            // Per ora usiamo un ID utente fisso per test
            article.setUserId("4");  // ID dell'utente attualmente loggato
            article.setDateDownloaded(LocalDateTime.now());
            
            // Salva l'articolo
            Article savedArticle = articleDAO.save(article);
            ctx.json(savedArticle);
            
        } catch (Exception e) {
            logger.error("Failed to save article: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Failed to save article: " + e.getMessage()));
        }
    }

    public void getUserArticles(Context ctx) {
        try {
            // Usa lo stesso ID utente fisso
            String userId = "4";  // ID dell'utente attualmente loggato
            
            List<Article> articles = articleDAO.findByUserId(userId);
            ctx.json(articles);
            
        } catch (Exception e) {
            logger.error("Failed to get user articles: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Failed to get user articles: " + e.getMessage()));
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

    public void deleteArticle(Context ctx) {
        try {
            String articleId = ctx.pathParam("id");
            String userId = "4"; // Stesso ID utente fisso
            
            logger.info("Deleting article {} for user {}", articleId, userId);
            boolean deleted = articleDAO.deleteArticle(articleId, userId);
            
            if (deleted) {
                logger.info("Article {} successfully deleted", articleId);
                ctx.status(204); // No Content
            } else {
                logger.warn("Article {} not found or not owned by user {}", articleId, userId);
                ctx.status(404).json(Map.of("error", "Article not found"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to delete article: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Failed to delete article: " + e.getMessage()));
        }
    }
} 