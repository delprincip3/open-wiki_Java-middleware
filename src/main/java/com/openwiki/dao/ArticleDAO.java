package com.openwiki.dao;

import com.openwiki.config.DatabaseConfig;
import com.openwiki.model.Article;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticleDAO {
    
    public Article save(Article article) throws SQLException {
        String sql = "INSERT INTO saved_articles (user_id, title, content, image_url) " +
                    "VALUES (?, ?, ?, ?)";
                    
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, Integer.parseInt(article.getUserId()));
            stmt.setString(2, article.getTitle());
            stmt.setString(3, article.getContent());
            stmt.setString(4, article.getImageUrl());
            
            stmt.executeUpdate();
            
            // Ottieni l'ID generato
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                article.setId(String.valueOf(rs.getInt(1)));
            }
            
            return article;
        }
    }
    
    public Optional<Article> findById(String id) throws SQLException {
        String sql = "SELECT * FROM saved_articles WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToArticle(rs));
            }
            return Optional.empty();
        }
    }
    
    public List<Article> findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM saved_articles WHERE user_id = ?";
        List<Article> articles = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                articles.add(mapResultSetToArticle(rs));
            }
            return articles;
        }
    }
    
    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(String.valueOf(rs.getInt("id")));
        article.setUserId(String.valueOf(rs.getInt("user_id")));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setImageUrl(rs.getString("image_url"));
        article.setDateDownloaded(rs.getTimestamp("date_downloaded").toLocalDateTime());
        return article;
    }
} 