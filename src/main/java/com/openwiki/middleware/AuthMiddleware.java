package com.openwiki.middleware;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class AuthMiddleware implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(AuthMiddleware.class);

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Escludi gli endpoint pubblici
        if (ctx.path().startsWith("/api/test") || 
            ctx.path().startsWith("/api/auth") ||
            ctx.path().equals("/api/wikipedia/featured") ||
            ctx.path().startsWith("/api/wikipedia/article/")) {
            return;
        }

        String userId = ctx.header("X-User-ID");
        
        if (userId == null || userId.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized: Missing or invalid user ID");
            error.put("code", "AUTH_REQUIRED");
            ctx.status(401).json(error);
            return;
        }

        ctx.attribute("userId", userId);
    }
} 