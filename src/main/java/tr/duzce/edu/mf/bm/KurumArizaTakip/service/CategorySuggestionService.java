package tr.duzce.edu.mf.bm.KurumArizaTakip.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Category;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.CategoryDao;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CategorySuggestionService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final CategoryCatalogService categoryCatalogService;
    private final Environment env;
    private final HttpClient httpClient;

    public CategorySuggestionService(CategoryCatalogService categoryCatalogService, Environment env) {
        this.categoryCatalogService = categoryCatalogService;
        this.env = env;
        this.httpClient = HttpClient.newHttpClient();
    }

    public Optional<Category> resolveCategory(Long categoryId, String title, String description) {
        if (categoryId != null) {
            return categoryCatalogService.ensureDefaultsAndList().stream()
                    .filter(category -> category.getId().equals(categoryId))
                    .findFirst();
        }
        return suggestCategory(title, description);
    }

    public Optional<Category> suggestCategory(String title, String description) {
        List<Category> categories = categoryCatalogService.ensureDefaultsAndList();

        if (categories.isEmpty()) {
            return Optional.empty();
        }

        String apiKey = env.getProperty("gemini.api.key", "").trim();

        if (apiKey.isEmpty()) {
            throw new IllegalStateException("GEMINI API KEY tanimli degil.");
        }

        String categoryName = requestCategoryName(apiKey, categories, title, description);

        Category matchedCategory = categories.stream()
                .filter(c -> normalize(c.getCatName()).equals(normalize(categoryName)))
                .findFirst()
                .or(() -> categories.stream()
                        .filter(c -> normalize(categoryName).contains(normalize(c.getCatName())))
                        .findFirst())
                .orElseThrow(() ->
                        new IllegalStateException("Gemini gecerli kategori donmedi: " + categoryName));

        return Optional.of(matchedCategory);
    }

    private String requestCategoryName(String apiKey,
                                       List<Category> categories,
                                       String title,
                                       String description) {

        String model = env.getProperty("gemini.model", "gemini-2.0-flash");
        String baseUrl = env.getProperty("gemini.url");

        String prompt = buildPrompt(categories, title, description);

        JSONObject payload = new JSONObject();

        JSONArray contents = new JSONArray();
        JSONObject contentObj = new JSONObject();
        JSONArray parts = new JSONArray();

        JSONObject part = new JSONObject();
        part.put("text", prompt);

        parts.add(part);
        contentObj.put("parts", parts);
        contents.add(contentObj);

        payload.put("contents", contents);

        String fullUrl = baseUrl + "/" + model + ":generateContent?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String detail = response.body();
                if (response.statusCode() == 404) {
                    throw new IllegalStateException(
                            "Gemini modeli veya endpoint bulunamadi. Model=" + model +
                                    ". HTTP 404. Response=" + detail
                    );
                }
                throw new IllegalStateException("Gemini istegi basarisiz. HTTP " + response.statusCode() + ". Response=" + detail);
            }

            return extractText(response.body());

        } catch (IOException e) {
            throw new IllegalStateException("Gemini baglanti hatasi.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Gemini istegi kesintiye ugradi.", e);
        }
    }

    private String extractText(String body) {

        JSONObject json = JSONObject.fromObject(body);

        JSONArray candidates = json.optJSONArray("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini response bos.");
        }

        JSONObject first = candidates.getJSONObject(0);
        JSONObject content = first.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");

        if (parts.isEmpty()) {
            throw new IllegalStateException("Gemini text donmedi.");
        }

        return parts.getJSONObject(0).getString("text").trim();
    }

    private String buildPrompt(List<Category> categories,
                               String title,
                               String description) {

        StringBuilder sb = new StringBuilder();

        sb.append("You classify IT support tickets.\n");
        sb.append("Choose exactly ONE category from list below.\n");
        sb.append("Return ONLY category name.\n\n");

        sb.append("Categories:\n");

        for (Category c : categories) {
            sb.append("- ").append(c.getCatName()).append("\n");
        }

        sb.append("\nTitle: ").append(safe(title)).append("\n");
        sb.append("Description: ").append(safe(description)).append("\n");

        return sb.toString();
    }

    private String safe(String val) {
        return val == null ? "" : val.trim();
    }

    private String normalize(String value) {

        if (value == null) return "";

        String normalized = value.toLowerCase(Locale.ROOT)
                .replace('ı', 'i')
                .replace('ğ', 'g')
                .replace('ü', 'u')
                .replace('ş', 's')
                .replace('ö', 'o')
                .replace('ç', 'c');

        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return normalized
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
