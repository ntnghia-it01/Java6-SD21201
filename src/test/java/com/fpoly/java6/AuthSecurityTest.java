package com.fpoly.java6;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.java6.controllers.AuthController;
import com.fpoly.java6.beans.RegisterBean;
import com.fpoly.java6.beans.LoginBean;
import com.fpoly.java6.entities.UserEntity;
import com.fpoly.java6.jpas.UserJPA;
import com.fpoly.java6.security.SecurityConfig;
import com.fpoly.java6.services.AuthService;
import com.fpoly.java6.services.ErrorServices;
import com.fpoly.java6.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(controllers = {AuthController.class, AuthSecurityTest.ProtectedController.class}, properties = {
    "app.jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
    "app.jwt.issuer=java6-api"
})
@Import({SecurityConfig.class, ErrorServices.class, AuthSecurityTest.ProtectedController.class})
class AuthSecurityTest {
    @Autowired MockMvc mvc;
    @Autowired JwtEncoder encoder;
    @MockitoBean AuthService auth;
    @MockitoBean UserJPA users;

    @RestController
    static class ProtectedController {
        @GetMapping("/protected") String protectedResource() { return "ok"; }
        @GetMapping("/products") String products() { return "products"; }
        @PostMapping("/products") String createProduct() { return "created"; }
    }

    private String token(Instant expiry) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("java6-api").subject("7")
                .issuedAt(now.minusSeconds(600)).expiresAt(expiry).build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @Test
    void authenticationEndpointsArePublicAndValidateInput() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"An\",\"email\":\"an@example.com\",\"password\":\"12345678\"}"))
                .andExpect(status().isCreated());
        verify(auth).register(new RegisterBean("An", "an@example.com", "12345678"));
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"email\":\"invalid\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.requestBody").exists());
        when(auth.login(new LoginBean("an@example.com", "12345678")))
                .thenReturn(new LoginResponse("token", "Bearer", 3600));
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"an@example.com\",\"password\":\"12345678\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("token"));
        when(auth.login(new LoginBean("an@example.com", "wrongpass")))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng"));
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"an@example.com\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointsRequireValidTokenForActiveUser() throws Exception {
        mvc.perform(get("/protected")).andExpect(status().isUnauthorized());
        mvc.perform(get("/products")).andExpect(status().isOk());
        mvc.perform(get("/protected").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        var user = new UserEntity();
        user.setId(7);
        user.setStatus((short) 1);
        user.setRole(1);
        when(users.findById(7)).thenReturn(Optional.of(user));
        mvc.perform(get("/protected").header("Authorization", "Bearer " + token(Instant.now().plusSeconds(600))))
                .andExpect(status().isOk());
        mvc.perform(post("/products").header("Authorization", "Bearer " + token(Instant.now().plusSeconds(600))))
                .andExpect(status().isForbidden());
        user.setRole(2);
        mvc.perform(post("/products").header("Authorization", "Bearer " + token(Instant.now().plusSeconds(600))))
                .andExpect(status().isOk());
        mvc.perform(get("/protected").header("Authorization", "Bearer " + token(Instant.now().minusSeconds(120))))
                .andExpect(status().isUnauthorized());
        user.setStatus((short) 0);
        mvc.perform(get("/protected").header("Authorization", "Bearer " + token(Instant.now().plusSeconds(600))))
                .andExpect(status().isUnauthorized());
    }
}
