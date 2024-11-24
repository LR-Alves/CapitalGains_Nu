package com.nu.capitalgains.api.app.configuration;


import com.nu.capitalgains.api.app.ports.input.TaxUseCase;
import com.nu.capitalgains.api.app.service.TaxCalculatorUseCase;
import com.nu.capitalgains.api.domain.service.TaxCalculatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public TaxCalculatorService taxCalculatorService() {
        return new TaxCalculatorService();
    }

    @Bean
    public TaxUseCase taxUseCase(TaxCalculatorService taxCalculatorService) {
        return new TaxCalculatorUseCase(taxCalculatorService);
    }
}