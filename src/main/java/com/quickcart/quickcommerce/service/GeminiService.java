package com.quickcart.quickcommerce.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickcart.quickcommerce.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient geminiWebClient;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Autowired
    public GeminiService(WebClient geminiWebClient, ProductService productService, ObjectMapper objectMapper) {
        this.geminiWebClient = geminiWebClient;
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    public Mono<List<Product>> getProductRecommendations(String query) {
        String prompt = "Recommend 3 to 5 product names from a quick commerce app based on '" + query + "'. " +
                "Only list product names as a comma-separated string. For example: Milk, Bread, Eggs, Butter, Cereal";

        System.out.println("🔍 [Gemini] Query: " + query);

        return geminiWebClient.post()
                .uri("models/gemini-1.5-flash-latest:generateContent")
                .header("Content-Type", "application/json")
                .bodyValue(createGeminiRequestBody(prompt))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> Mono.fromCallable(() -> {
                    String rawText = extractTextFromGeminiResponse(response);
                    System.out.println("📨 [Gemini] Raw response: " + rawText);
                    return parseCommaSeparatedNames(rawText);
                }))
                .flatMap(recommendedNames -> {
                    System.out.println("✅ [Gemini] Recommended terms: " + recommendedNames);
                    List<Product> allProducts = productService.getAllProducts();

                    List<Product> matchedProducts = allProducts.stream()
                            .filter(product -> recommendedNames.stream()
                                    .anyMatch(name -> product.getName().toLowerCase().contains(name.toLowerCase())))
                            .distinct()
                            .collect(Collectors.toList());

                    if (matchedProducts.isEmpty()) {
                        // Fallback: show best matches based on query itself
                        return Mono.just(allProducts.stream()
                                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                                .limit(3)
                                .collect(Collectors.toList()));
                    } else {
                        return Mono.just(matchedProducts);
                    }
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    System.err.println("❌ WebClient error (Gemini recommendation): " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                    return Mono.just(List.of());
                })
                .onErrorResume(Exception.class, e -> {
                    System.err.println("❌ General error (Gemini recommendation): " + e.getMessage());
                    return Mono.just(List.of());
                });
    }

    private Map<String, Object> createGeminiRequestBody(String promptText) {
        return Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", promptText)))));
    }

    private String extractTextFromGeminiResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidatesNode = rootNode.path("candidates");
            if (candidatesNode.isArray() && !candidatesNode.isEmpty()) {
                JsonNode textNode = candidatesNode.get(0)
                        .path("content").path("parts").get(0).path("text");
                if (!textNode.isMissingNode()) {
                    return textNode.asText();
                }
            }
            System.err.println("⚠️ Gemini response missing expected structure: " + response);
            return "";
        } catch (Exception e) {
            System.err.println("❌ Error parsing Gemini response: " + e.getMessage());
            return "";
        }
    }

    private List<String> parseCommaSeparatedNames(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(rawText.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    // --- Chatbot ---
    public Mono<String> getChatbotResponse(String userMessage) {
        String prompt = "You are a helpful customer support chatbot for a quick commerce app. " +
                "Respond to the following user message: " + userMessage;

        return geminiWebClient.post()
                .uri("models/gemini-1.5-flash-latest:generateContent")
                .header("Content-Type", "application/json")
                .bodyValue(createGeminiRequestBody(prompt))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractTextFromGeminiResponse)
                .onErrorResume(WebClientResponseException.class, e -> {
                    System.err.println("❌ WebClient error (Gemini chatbot): " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                    return Mono.just("Sorry, I'm currently unable to provide a response.");
                })
                .onErrorResume(Exception.class, e -> {
                    System.err.println("❌ General error (Gemini chatbot): " + e.getMessage());
                    return Mono.just("Sorry, I'm currently unable to provide a response.");
                });
    }

    // --- Smart Search ---
    public Mono<List<Product>> intelligentProductSearch(String query) {
        String prompt = "Identify key product categories or names from the search query: '" + query + "'. " +
                "Only list identified terms as a comma-separated string. Do not include any other text or formatting.";

        return geminiWebClient.post()
                .uri("models/gemini-1.5-flash-latest:generateContent")
                .header("Content-Type", "application/json")
                .bodyValue(createGeminiRequestBody(prompt))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> Mono.fromCallable(() -> {
                    String rawText = extractTextFromGeminiResponse(response);
                    System.out.println("🔎 [Search] Extracted terms: " + rawText);
                    return parseCommaSeparatedNames(rawText);
                }))
                .flatMap(searchTerms -> {
                    List<Product> allProducts = productService.getAllProducts();
                    return Flux.fromIterable(allProducts)
                            .filter(p -> searchTerms.stream().anyMatch(term ->
                                    p.getName().toLowerCase().contains(term.toLowerCase()) ||
                                            p.getDescription().toLowerCase().contains(term.toLowerCase()) ||
                                            p.getCategory().toLowerCase().contains(term.toLowerCase()) ||
                                            p.getBrand().toLowerCase().contains(term.toLowerCase())))
                            .distinct()
                            .collectList();
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    System.err.println("❌ WebClient error (Gemini search): " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                    return Mono.just(List.of());
                })
                .onErrorResume(Exception.class, e -> {
                    System.err.println("❌ General error (Gemini search): " + e.getMessage());
                    return Mono.just(List.of());
                });
    }
}
