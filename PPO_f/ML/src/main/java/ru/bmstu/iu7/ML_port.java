package ru.bmstu.iu7;

import ru.bmstu.iu7.API.AppLogger;
import ru.bmstu.iu7.API.IML_port;
import ru.bmstu.iu7.API.model.ATag;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ML_port implements IML_port {

    private String olamaHost;
    private final AppLogger logger;

    public ML_port(String host, AppLogger applogger) {
        this.olamaHost = host;
        this.logger = applogger;
        logInfo("ML_port initialized with host {}", olamaHost);
    }

    public ML_port(String host) {
        this(host, null);
    }

    @Override
    public List<ATag> get_tags_names(String question, String answer, List<ATag> tags)
            throws IOException, InterruptedException {

        logInfo("Generating tags for question '{}' and answer '{}'", question, answer);

        String requestBody = buildPrompt(question, answer, tags);
        String responseBody = sendRequest(requestBody);
        List<String> tagNames = parseResponseTags(responseBody);

        return matchTags(tagNames, tags);
    }

    // ======================== Вспомогательные методы ========================

    private void logInfo(String message, Object... args) {
        if (logger != null) logger.info(message, args);
    }

    private String buildPrompt(String question, String answer, List<ATag> tags) {
        StringBuilder tagsList = new StringBuilder();
        for (ATag tag : tags) tagsList.append(tag.getName()).append(",");
        return "{\n" +
                "  \"stream\": false,\n" +
                "  \"model\": \"gemma3:4b-it-qat\",\n" +
                "  \"prompt\": \"Given the question: " + question +
                " Here is the answer: " + answer +
                " Available tags: " + tagsList +
                " Extract at least two tags from the answer and respond only with the tags, separated by commas.\"\n" +
                "}";
    }

    private String sendRequest(String requestBody) throws IOException, InterruptedException {
        logInfo("Sending request to ML service at {}", olamaHost);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(olamaHost))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        logInfo("Received response: {}", response.body());
        return response.body();
    }

    private List<String> parseResponseTags(String responseBody) {
        Map<String, String> parsed;
        try {
            parsed = parseJson(responseBody);
        } catch (IllegalArgumentException e) {
            logError("Failed to parse JSON response", e);
            throw e;
        }

        List<String> tags = new ArrayList<>(List.of(parsed.getOrDefault("response", "").split(",")));
        if (!tags.isEmpty()) {
            int lastIndex = tags.size() - 1;
            String last = tags.get(lastIndex);
            if (last.contains("\\n")) last = last.substring(0, last.length() - 2);
            tags.set(lastIndex, last);
        }
        return tags;
    }

    private List<ATag> matchTags(List<String> tagNames, List<ATag> tags) {
        List<ATag> matched = new ArrayList<>();
        for (String tagName : tagNames) {
            for (ATag tag : tags) {
                if (tag.getName().equals(tagName)) {
                    matched.add(tag);
                    logInfo("Matched tag: {}", tagName);
                }
            }
        }
        logInfo("Total matched tags: {}", matched.size());
        return matched;
    }

    // ======================== JSON PARSING ========================

    private Map<String, String> parseJson(String jsonStr) {
        jsonStr = trimBraces(jsonStr);
        List<String> tokens = splitTokens(jsonStr);
        return extractKeyValue(tokens);
    }

    private String trimBraces(String jsonStr) {
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
            return jsonStr.substring(1, jsonStr.length() - 1).trim();
        } else {
            logError("Invalid JSON object: {}", new IllegalArgumentException(jsonStr));
            throw new IllegalArgumentException("Invalid JSON object");
        }
    }

    private List<String> splitTokens(String jsonStr) {
        boolean inQuotes = false;
        StringBuilder keyOrValue = new StringBuilder();
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (c == '"') inQuotes = !inQuotes;
            if (c == ',' && !inQuotes) {
                tokens.add(keyOrValue.toString().trim());
                keyOrValue.setLength(0);
            } else {
                keyOrValue.append(c);
            }
        }
        if (!keyOrValue.isEmpty()) tokens.add(keyOrValue.toString().trim());
        return tokens;
    }

    private Map<String, String> extractKeyValue(List<String> tokens) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String token : tokens) {
            int colonIndex = findColon(token);
            if (colonIndex == -1) continue;
            String key = stripQuotes(token.substring(0, colonIndex).trim());
            String value = stripQuotes(token.substring(colonIndex + 1).trim());
            result.put(key, value);
        }
        return result;
    }

    private int findColon(String token) {
        boolean inQuotes = false;
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c == '"') inQuotes = !inQuotes;
            if (c == ':' && !inQuotes) return i;
        }
        return -1;
    }

    private String stripQuotes(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) return str.substring(1, str.length() - 1);
        return str;
    }

    private void logError(String message, Exception e) {
        if (logger != null) logger.error(message, e);
    }
}
