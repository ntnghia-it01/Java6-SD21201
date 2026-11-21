package com.fpoly.java6.services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fpoly.java6.entities.UserEntity;
import com.fpoly.java6.beans.LoginBean;
import com.fpoly.java6.beans.RegisterBean;
import com.fpoly.java6.jpas.UserJPA;
import com.fpoly.java6.response.LoginResponse;

@Service
public class AuthService {
    private final UserJPA users;
    private final PasswordEncoder passwords;
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationSeconds;

    public AuthService(UserJPA users, PasswordEncoder passwords, JwtEncoder jwtEncoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration-seconds}") long expirationSeconds) {
        if (expirationSeconds <= 0) throw new IllegalArgumentException("Thời hạn JWT phải lớn hơn 0");
        this.users = users;
        this.passwords = passwords;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public void register(RegisterBean bean) {
        String name = bean.getName();
        String email = bean.getEmail();
        String password = bean.getPassword();
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được vượt quá 72 byte UTF-8");
        }
        if (users.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng");
        }
        UserEntity user = new UserEntity();
        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwords.encode(password));
        user.setRole(1);
        user.setStatus((short) 1);
        try {
            users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            if (users.existsByEmailIgnoreCase(normalizedEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng");
            }
            throw e;
        }
    }

    public LoginResponse login(LoginBean bean) {
        String email = bean.getEmail();
        String password = bean.getPassword();
        var user = users.findByEmailIgnoreCase(email.trim()).orElse(null);
        if (password.getBytes(StandardCharsets.UTF_8).length > 72 || user == null || !Short.valueOf((short) 1).equals(user.getStatus())
                || !passwords.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng");
        }
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet
        		.builder()
        		.issuer(issuer)
        		.subject(user.getId().toString())
        		.claim("email", user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", expirationSeconds);
    }
}
