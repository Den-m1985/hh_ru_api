package com.example.service.common;

import com.example.RedisTestConfig;
import com.example.dto.StatePayload;
import com.example.enums.Device;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class OAuthStateGeneratorTest {
    @Autowired
    private OAuthStateGenerator stateGenerator;

    @Test
    void testGenerateAndParseState() {
        int userId = 99;
        Device device = Device.IOS;

        String encrypted = stateGenerator.generateEncryptedState(userId, device);
        assertNotNull(encrypted);

        StatePayload result = stateGenerator.parseState(encrypted);
        assertEquals(userId, result.userId());
        assertEquals(device, result.device());
    }
}
