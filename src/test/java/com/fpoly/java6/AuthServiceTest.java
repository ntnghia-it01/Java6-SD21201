package com.fpoly.java6;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.fpoly.java6.entities.UserEntity;
import com.fpoly.java6.beans.LoginBean;
import com.fpoly.java6.beans.RegisterBean;
import com.fpoly.java6.jpas.UserJPA;
import com.fpoly.java6.services.AuthService;

class AuthServiceTest {
    private final UserJPA users = mock(UserJPA.class);
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder();
    private final SecretKeySpec key = new SecretKeySpec("0123456789abcdef0123456789abcdef".getBytes(), "HmacSHA256");
    private final AuthService service = new AuthService(users, passwords, new NimbusJwtEncoder(new ImmutableSecret<>(key)), "java6-api", 3600);

    @Test
    void registerHashesPasswordAndAlwaysCreatesUserRole() {
        service.register(new RegisterBean("An", "AN@EXAMPLE.COM", "password123"));
        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(users).saveAndFlush(captor.capture());
        var user = captor.getValue();
        assertEquals("an@example.com", user.getEmail());
        assertEquals(1, user.getRole());
        assertEquals((short) 1, user.getStatus());
        assertNotEquals("password123", user.getPasswordHash());
        assertTrue(passwords.matches("password123", user.getPasswordHash()));
    }

    @Test
    void loginRejectsBadCredentialsAndReturnsSignedExpiringToken() {
        var user = new UserEntity();
        user.setId(7);
        user.setStatus((short) 1);
        user.setPasswordHash(passwords.encode("password123"));
        when(users.findByEmailIgnoreCase("an@example.com")).thenReturn(Optional.of(user));
        assertThrows(ResponseStatusException.class, () -> service.login(new LoginBean("an@example.com", "wrong")));
        var token = service.login(new LoginBean("an@example.com", "password123"));
        var decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        var jwt = decoder.decode(token.getAccessToken());
        assertEquals("7", jwt.getSubject());
        assertEquals(3600, jwt.getExpiresAt().getEpochSecond() - jwt.getIssuedAt().getEpochSecond());
        user.setStatus((short) 0);
        assertThrows(ResponseStatusException.class, () -> service.login(new LoginBean("an@example.com", "password123")));
    }

    @Test
    void duplicateEmailIsRejected() {
        when(users.existsByEmailIgnoreCase(anyString())).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> service.register(new RegisterBean("An", "an@example.com", "password123")));
        verify(users, never()).saveAndFlush(any());
    }
}
