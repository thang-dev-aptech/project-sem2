package com.example.gympro.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VietQRService {

    // API tạo mã QR của VietQR (Phiên bản 2)
    private static final String API_URL = "https://api.vietqr.io/v2/generate";

    /**
     * Call API to get QR image string (Base64)
     * @param bankBin Bank code (e.g. MB = 970422)
     * @param accountNo Account number
     * @param accountName Account holder name (Uppercase, no accents)
     * @param amount Amount
     * @param content Transfer content
     * @return Image URL string (data:image/png;base64...), or null if error
     */
    public String generateQRCodeBase64(String bankBin, String accountNo, String accountName, double amount, String content) {
        try {
            // 1. Create JSON packet in memory (instead of creating .json file)
            JsonObject jsonBody = new JsonObject();
            jsonBody.addProperty("accountNo", accountNo);
            jsonBody.addProperty("accountName", accountName);
            jsonBody.addProperty("acqId", bankBin);
            jsonBody.addProperty("amount", (long) amount); // API requires integer
            jsonBody.addProperty("addInfo", content);
            jsonBody.addProperty("format", "text");
            jsonBody.addProperty("template", "compact2"); // QR template

            // 2. Prepare HTTP Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    // These headers help API identify client (can be demo)
                    .header("x-client-id", "demo-client-id")
                    .header("x-api-key", "demo-api-key")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();

            // 3. Send and wait for response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Parse JSON Response
            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

            // Check if successful (code "00" is OK)
            String code = responseJson.get("code").getAsString();
            if ("00".equals(code)) {
                // Get image data from response packet
                JsonObject data = responseJson.getAsJsonObject("data");
                return data.get("qrDataURL").getAsString();
            } else {
                System.err.println("VietQR Error: " + responseJson.get("desc").getAsString());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}