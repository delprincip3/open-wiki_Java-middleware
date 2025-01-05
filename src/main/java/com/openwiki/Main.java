package com.openwiki;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import com.openwiki.config.DatabaseConfig;
import com.openwiki.dao.ArticleDAO;
import java.sql.Connection;
import com.openwiki.controller.WikiController;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.allowHost("http://localhost:3000"); // Frontend React
                    it.allowCredentials = true;
                });
            });
        });

        WikiController wikiController = new WikiController();

        // Test endpoint base
        app.get("/api/test", Main::testHandler);
        
        // Test endpoint per il database
        app.get("/api/test/db", Main::testDatabase);

        // Wikipedia endpoints
        app.get("/api/wikipedia/search", wikiController::search);
        app.get("/api/wikipedia/article/{title}", wikiController::getArticle);
        app.post("/api/articles", wikiController::saveArticle);

        app.start(8080);
    }

    private static void testHandler(Context ctx) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "OpenWiki Middleware is running!");
        response.put("status", "success");
        ctx.json(response);
    }
    
    private static void testDatabase(Context ctx) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Test connessione database
            Connection conn = DatabaseConfig.getConnection();
            conn.close();
            
            // Test ArticleDAO con un ID utente valido
            ArticleDAO articleDAO = new ArticleDAO();
            articleDAO.findByUserId("1"); // Usa "1" invece di "test-user"
            
            response.put("message", "Database connection and queries working!");
            response.put("status", "success");
            ctx.json(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Database test failed: " + e.getMessage());
            ctx.status(500).json(response);
        }
    }
} 