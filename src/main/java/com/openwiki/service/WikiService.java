package com.openwiki.service;

import com.openwiki.model.WikiSearchResult;
import com.openwiki.model.Article;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WikiService {
    private static final String WIKI_API_URL = "https://it.wikipedia.org/w/api.php";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WikiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<WikiSearchResult> search(String query, int limit) throws Exception {
        String url = WIKI_API_URL + "?action=query&list=search&srsearch=" + 
                    URLEncoder.encode(query, StandardCharsets.UTF_8) +
                    "&format=json&srlimit=" + limit;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        
        List<WikiSearchResult> results = new ArrayList<>();
        JsonNode searchResults = root.path("query").path("search");
        
        for (JsonNode result : searchResults) {
            WikiSearchResult searchResult = new WikiSearchResult();
            searchResult.setTitle(result.path("title").asText());
            searchResult.setExcerpt(result.path("snippet").asText());
            searchResult.setPageId(result.path("pageid").asText());
            
            // Costruisci l'URL della pagina
            String pageUrl = "https://it.wikipedia.org/wiki/" + 
                           URLEncoder.encode(result.path("title").asText().replace(" ", "_"), 
                           StandardCharsets.UTF_8);
            searchResult.setUrl(pageUrl);
            
            results.add(searchResult);
        }
        
        return results;
    }

    public Article getArticle(String title) throws Exception {
        String url = WIKI_API_URL + "?action=query&prop=extracts|pageimages&titles=" + 
                    URLEncoder.encode(title, StandardCharsets.UTF_8) +
                    "&format=json&explaintext=1&pithumbsize=500";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        
        JsonNode pages = root.path("query").path("pages");
        JsonNode firstPage = pages.elements().next();
        
        Article article = new Article();
        article.setTitle(firstPage.path("title").asText());
        article.setContent(firstPage.path("extract").asText());
        article.setPageId(firstPage.path("pageid").asText());
        
        // Imposta l'URL dell'immagine se disponibile
        if (firstPage.has("thumbnail")) {
            article.setImageUrl(firstPage.path("thumbnail").path("source").asText());
        }
        
        // Costruisci l'URL della pagina
        article.setWikiUrl("https://it.wikipedia.org/wiki/" + 
                          URLEncoder.encode(article.getTitle().replace(" ", "_"), 
                          StandardCharsets.UTF_8));
        
        return article;
    }
} 