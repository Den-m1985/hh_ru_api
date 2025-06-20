package com.example.service.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class CryptoServiceTest {
    @Autowired
    private CryptoService cryptoService;


    @Test
    void testEncryptAndDecrypt() {
        String original = "{\"userId\":42,\"device\":\"ANDROID\"}";
        String encrypted = cryptoService.encrypt(original);
        String decrypted = cryptoService.decrypt(encrypted);
        assertEquals(original, decrypted);
    }

    @Test
    void testDecryptInvalidData() {
        assertThrows(IllegalStateException.class, () -> cryptoService.decrypt("invalid_base64"));
    }
}
