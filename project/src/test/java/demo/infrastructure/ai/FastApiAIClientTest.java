package demo.infrastructure.ai;

import demo.config.AIProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class FastApiAIClientTest {
    @Test
    void sendsFeatureRequestToFastApiPredictEndpoint() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        AIProperties properties = new AIProperties();
        properties.setUrl("http://ai-service.test");
        server.expect(requestTo("http://ai-service.test/predict")).andExpect(method(POST))
                .andExpect(content().json("{\"foodId\":7,\"features\":{\"transportTemperature\":4.2}}"))
                .andRespond(withSuccess("{\"score\":92.5,\"level\":\"A\",\"confidence\":0.88,\"modelVersion\":\"mock-v1\"}", MediaType.APPLICATION_JSON));
        FoodFeatureRequest request = new FoodFeatureRequest();
        request.setFoodId(7L); request.setFeatures(Collections.singletonMap("transportTemperature", new BigDecimal("4.2")));
        PredictionResponse response = new FastApiAIClient(restTemplate, properties).predict(request);
        assertEquals(new BigDecimal("92.5"), response.getScore());
        assertEquals("A", response.getLevel());
        assertEquals("mock-v1", response.getModelVersion());
        server.verify();
    }
}
