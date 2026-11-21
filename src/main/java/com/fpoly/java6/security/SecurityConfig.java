package com.fpoly.java6.security;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;import org.aspectj.weaver.CrosscuttingMembers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import com.fpoly.java6.jpas.UserJPA;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecretKey jwtKey(@Value("${app.jwt.secret}") String secret) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT_SECRET phải là Base64 hợp lệ", e);
        }
        if (bytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET phải chứa ít nhất 32 byte");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey key) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey key, @Value("${app.jwt.issuer}") String issuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, UserJPA users) throws Exception {
        return http
//        		.csrf(new Customizer<CsrfConfigurer<HttpSecurity>>() {
//					@Override
//					public void customize(CsrfConfigurer<HttpSecurity> t) {
//						t.disable();
//					}
//        		})
//        		IP
        		.cors(cros -> cros.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(cache -> cache.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products", "/products/**", "/categories", "/categories/**", "/images-upload/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER"))
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(token -> authenticate(token, users))))
                .build();
    }

    private JwtAuthenticationToken authenticate(Jwt token, UserJPA users) {
        Integer id;
        try {
            id = Integer.valueOf(token.getSubject());
        } catch (RuntimeException e) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"));
        }
        var user = users.findById(id).orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error("invalid_token")));
        if (!Short.valueOf((short) 1).equals(user.getStatus())) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"));
        }
        String role = Integer.valueOf(2).equals(user.getRole()) ? "ROLE_ADMIN" : "ROLE_USER";
        return new JwtAuthenticationToken(token, java.util.List.of(new SimpleGrantedAuthority(role)));
    }
}
