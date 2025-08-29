package com.bajajfinserv.webhookqualifier.config;

import com.bajajfinserv.webhookqualifier.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    
    private final WebhookService webhookService;
    
    @Autowired
    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Execute the full webhook qualification flow on startup
        webhookService.executeFullFlow();
    }
}
