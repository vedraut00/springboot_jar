package com.bajajfinserv.webhookqualifier.service;

import com.bajajfinserv.webhookqualifier.model.SolutionRequest;
import com.bajajfinserv.webhookqualifier.model.WebhookRequest;
import com.bajajfinserv.webhookqualifier.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private static final String BASE_URL = "https://bfhldevapigw.healthrx.co.in/hiring";
    
    private final WebClient webClient;
    private final SqlSolver sqlSolver;
    
    @Autowired
    public WebhookService(SqlSolver sqlSolver) {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.sqlSolver = sqlSolver;
    }
    
    public void executeFullFlow() {
        try {
            logger.info("Starting webhook qualification process...");
            
            // Step 1: Generate webhook
            WebhookRequest request = new WebhookRequest("John Doe", "REG12347", "john@example.com");
            WebhookResponse response = generateWebhook(request);
            
            if (response != null && response.getWebhook() != null && response.getAccessToken() != null) {
                logger.info("Webhook generated successfully");
                
                // Step 2: Solve SQL problem
                String sqlSolution = sqlSolver.solveProblem(request.getRegNo());
                logger.info("SQL problem solved");
                
                // Step 3: Submit solution
                submitSolution(sqlSolution, response.getAccessToken());
                logger.info("Solution submitted successfully");
            } else {
                logger.error("Failed to generate webhook");
            }
        } catch (Exception e) {
            logger.error("Error in webhook qualification process", e);
        }
    }
    
    private WebhookResponse generateWebhook(WebhookRequest request) {
        try {
            return webClient.post()
                    .uri("/generateWebhook/JAVA")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(WebhookResponse.class)
                    .block();
        } catch (Exception e) {
            logger.error("Error generating webhook", e);
            return null;
        }
    }
    
    private void submitSolution(String sqlQuery, String accessToken) {
        try {
            SolutionRequest solution = new SolutionRequest(sqlQuery);
            
            String result = webClient.post()
                    .uri("/testWebhook/JAVA")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .bodyValue(solution)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            logger.info("Solution submission result: {}", result);
        } catch (Exception e) {
            logger.error("Error submitting solution", e);
        }
    }
}
