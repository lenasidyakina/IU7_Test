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
        if (logger != null) logger.info("ML_port initialized with host {}", olamaHost);
    }

    public ML_port(String host) {
        this(host, null);
    }

    private Map<String, String> parseJson(String jsonStr) {
        Map<String, String> result = new LinkedHashMap<>();
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1).trim();
        } else {
            if (logger != null) logger.error("Invalid JSON object: {}", jsonStr);
            throw new IllegalArgumentException("Invalid JSON object");
        }

        boolean inQuotes = false;
        StringBuilder keyOrValue = new StringBuilder();
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (c == '\"') inQuotes = !inQuotes;
            if (c == ',' && !inQuotes) {
                tokens.add(keyOrValue.toString().trim());
                keyOrValue.setLength(0);
            } else {
                keyOrValue.append(c);
            }
        }
        if (!keyOrValue.isEmpty()) tokens.add(keyOrValue.toString().trim());

        for (String token : tokens) {
            int colonIndex = -1;
            boolean quoteFlag = false;
            for (int i = 0; i < token.length(); i++) {
                if (token.charAt(i) == '"') quoteFlag = !quoteFlag;
                if (token.charAt(i) == ':' && !quoteFlag) {
                    colonIndex = i;
                    break;
                }
            }
            if (colonIndex == -1) continue;

            String key = stripQuotes(token.substring(0, colonIndex).trim());
            String value = stripQuotes(token.substring(colonIndex + 1).trim());
            result.put(key, value);
        }
        return result;
    }

    private String stripQuotes(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) return str.substring(1, str.length() - 1);
        return str;
    }

    @Override
    public List<ATag> get_tags_names(String question, String answer, List<ATag> tags) throws IOException, InterruptedException {
        if (logger != null) logger.info("Generating tags for question '{}' and answer '{}'", question, answer);

        StringBuilder tags_name_list = new StringBuilder();
        for (ATag tag : tags) tags_name_list.append(tag.getName()).append(",");

        String requestBody = "{\"stream\": false, \"model\": \"gemma3:4b-it-qat\", \"prompt\": \"" +
                "Given the question: " + question +
                " Here is the answer: " + answer +
                " Available tags: " + tags_name_list +
                " Extract at least two tags from the answer and respond with nothing but the tags, separated by commas.\"}";

        if (logger != null) logger.info("Sending request to ML service at {}", olamaHost);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(olamaHost))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (logger != null) logger.info("Received response: {}", response.body());

        Map<String, String> parsed;
        try {
            parsed = parseJson(response.body());
        } catch (IllegalArgumentException e) {
            if (logger != null) logger.error("Failed to parse JSON response", e);
            throw e;
        }

        List<String> tags_names = new ArrayList<>(List.of(parsed.getOrDefault("response", "").split(",")));

        if (!tags_names.isEmpty()) {
            int lastIndex = tags_names.size() - 1;
            String last = tags_names.get(lastIndex);
            if (last.contains("\\n")) last = last.substring(0, last.length() - 2);
            tags_names.set(lastIndex, last);
        }

        List<ATag> answer_tags = new ArrayList<>();
        for (String tag_name : tags_names) {
            for (ATag tag : tags) {
                if (tag.getName().equals(tag_name)) {
                    answer_tags.add(tag);
                    if (logger != null) logger.info("Matched tag: {}", tag_name);
                }
            }
        }

        if (logger != null) logger.info("Total matched tags: {}", answer_tags.size());
        return answer_tags;
    }
}
