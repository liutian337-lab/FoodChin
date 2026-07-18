package demo.infrastructure.ai;

import demo.config.AIProperties;
import demo.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class FastApiAIClient implements AIClient {
    private final RestTemplate restTemplate;
    private final AIProperties properties;

    @Autowired
    public FastApiAIClient(AIProperties properties) { this(new RestTemplate(), properties); }

    FastApiAIClient(RestTemplate restTemplate, AIProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public PredictionResponse predict(FoodFeatureRequest request) {
        try {
            ResponseEntity<PredictionResponse> response = restTemplate.postForEntity(endpoint(), request, PredictionResponse.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BusinessException(502, "AI service returned an invalid prediction response");
            }
            return response.getBody();
        } catch (RestClientException exception) {
            throw new BusinessException(502, "AI service invocation failed: " + exception.getMessage());
        }
    }

    private String endpoint() {
        if (properties.getUrl() == null || properties.getUrl().trim().isEmpty()) throw new BusinessException(500, "AI service URL is not configured");
        return properties.getUrl().replaceAll("/+$", "") + "/predict";
    }
}
