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
     * Hàm gọi API để lấy chuỗi ảnh QR (Base64)
     * @param bankBin Mã số ngân hàng (Ví dụ MB = 970422)
     * @param accountNo Số tài khoản
     * @param accountName Tên chủ tài khoản (Viết hoa không dấu)
     * @param amount Số tiền
     * @param content Nội dung chuyển khoản
     * @return Chuỗi URL ảnh (data:image/png;base64...), hoặc null nếu lỗi
     */
    public String generateQRCodeBase64(String bankBin, String accountNo, String accountName, double amount, String content) {
        try {
            // 1. Tạo gói tin JSON trong bộ nhớ (Thay vì tạo file .json)
            JsonObject jsonBody = new JsonObject();
            jsonBody.addProperty("accountNo", accountNo);
            jsonBody.addProperty("accountName", accountName);
            jsonBody.addProperty("acqId", bankBin);
            jsonBody.addProperty("amount", (long) amount); // API yêu cầu số nguyên
            jsonBody.addProperty("addInfo", content);
            jsonBody.addProperty("format", "text");
            jsonBody.addProperty("template", "compact2"); // Mẫu giao diện QR

            // 2. Chuẩn bị gửi thư (HTTP Request)
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    // Các header này giúp API nhận diện client (có thể để demo)
                    .header("x-client-id", "demo-client-id")
                    .header("x-api-key", "demo-api-key")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();

            // 3. Gửi đi và chờ phản hồi
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Đọc thư trả lời (Parse JSON Response)
            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

            // Kiểm tra xem thành công không (code "00" là OK)
            String code = responseJson.get("code").getAsString();
            if ("00".equals(code)) {
                // Lấy dữ liệu ảnh từ trong gói tin trả về
                JsonObject data = responseJson.getAsJsonObject("data");
                return data.get("qrDataURL").getAsString();
            } else {
                System.err.println("Lỗi VietQR: " + responseJson.get("desc").getAsString());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}