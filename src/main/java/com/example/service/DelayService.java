package com.example.service;

import com.example.util.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DelayService {
    private final ApplicationProperties properties;
    private final Random random = new Random();

    public void sleepRandom() throws InterruptedException {
        float delay = getRandomInterval(properties.getApplyIntervalMin(), properties.getApplyIntervalMax());
        log.debug("Sleeping for {} seconds", delay);
        Thread.sleep((long) (delay * 1000));
    }

    public float getRandomInterval(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

}
