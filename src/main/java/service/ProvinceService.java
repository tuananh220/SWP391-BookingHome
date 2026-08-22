package service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service tích hợp Vietnam Provinces Open API v2 (Cơ cấu 34 tỉnh/thành phố 2025/2026).
 */
public class ProvinceService {

    private static final Logger LOGGER = Logger.getLogger(ProvinceService.class.getName());
    private static final String API_BASE_URL = "https://provinces.open-api.vn/api/v2";

    private final HttpClient httpClient;
    private static String cachedProvincesJson = null;
    private static final Map<Integer, String> cachedWardsByProvince = new ConcurrentHashMap<>();

    public ProvinceService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Lấy danh sách toàn bộ Tỉnh / Thành phố dạng JSON.
     * Có cache in-memory để tăng tốc phản hồi.
     */
    public synchronized String getProvincesJson() {
        if (cachedProvincesJson != null && !cachedProvincesJson.isEmpty()) {
            return cachedProvincesJson;
        }

        String url = API_BASE_URL + "/p/";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                cachedProvincesJson = response.body();
                return cachedProvincesJson;
            } else {
                LOGGER.log(Level.WARNING, "Failed to fetch provinces, status code: {0}", response.statusCode());
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching provinces from API", ex);
            Thread.currentThread().interrupt();
        }

        return "[]";
    }

    /**
     * Lấy danh sách Xã / Phường / Đơn vị cấp dưới theo mã Tỉnh / Thành phố dạng JSON.
     */
    public String getWardsByProvinceJson(int provinceCode) {
        if (provinceCode <= 0) {
            return "[]";
        }

        if (cachedWardsByProvince.containsKey(provinceCode)) {
            return cachedWardsByProvince.get(provinceCode);
        }

        String url = API_BASE_URL + "/w/?province=" + provinceCode;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                cachedWardsByProvince.put(provinceCode, body);
                return body;
            } else {
                LOGGER.log(Level.WARNING, "Failed to fetch wards for province {0}, status code: {1}",
                        new Object[]{provinceCode, response.statusCode()});
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching wards for province " + provinceCode, ex);
            Thread.currentThread().interrupt();
        }

        return "[]";
    }
}
