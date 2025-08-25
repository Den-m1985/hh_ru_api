package com.example.service.factory;

import com.example.service.strategy.IOAuthStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class OAuthStrategyFactory {
    private final Map<String, IOAuthStrategy> strategies;

    public IOAuthStrategy getStrategy(String provider) {
        IOAuthStrategy strategy = strategies.get(provider.toLowerCase() + "Strategy");
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
        return strategy;
    }
}
