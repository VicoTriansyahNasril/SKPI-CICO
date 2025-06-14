package com.tujuhsembilan.springboot_boilerplate.config;

import com.skpijtk.springboot_boilerplate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "myDummySecretKeyForJWTThatIsLongEnough12345");
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtTokenProvider.generateToken(1L, User.Role.ADMIN);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("1", jwtTokenProvider.getUserIdFromToken(token));
        assertEquals("ADMIN", jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "this.is.invalid";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
}
