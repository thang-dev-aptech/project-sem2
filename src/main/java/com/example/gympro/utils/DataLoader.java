package com.example.gympro.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DataLoader {
    private static final Gson gson = new Gson();

    /**
     * Load JSON data from resources folder
     * @param resourcePath Path to JSON file (e.g., "data/dashboard-stats.json")
     * @return JsonObject containing the parsed JSON data
     */
    public static JsonObject loadJsonData(String resourcePath) {
        try (InputStream inputStream = DataLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                return gson.fromJson(reader, JsonObject.class);
            }
        } catch (IOException e) {
            System.err.println("Error loading data from " + resourcePath + ": " + e.getMessage());
            return new JsonObject();
        }
    }

    /**
     * Get a specific array from JSON data
     * @param resourcePath Path to JSON file
     * @param arrayName Name of the array in the JSON
     * @return JsonArray containing the requested data
     */
    public static com.google.gson.JsonArray getJsonArray(String resourcePath, String arrayName) {
        JsonObject data = loadJsonData(resourcePath);
        if (data.has(arrayName)) {
            return data.getAsJsonArray(arrayName);
        }
        return new com.google.gson.JsonArray();
    }
}
