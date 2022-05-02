package com.persoff68.fatodo.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class JwtTokenFilterIT {

    @Value("${test.jwt.user}")
    String userJwt;
    @Value("${test.jwt.invalid-expired}")
    String invalidExpiredJwt;
    @Value("${test.jwt.invalid-format}")
    String invalidFormatJwt;
    @Value("${test.jwt.invalid-wrong-token}")
    String invalidWrongTokenJwt;
    @Value("${test.jwt.invalid-wrong-uuid}")
    String invalidWrongUuidJwt;

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void testSuccessfulAuthorization() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userJwt);
        mvc.perform(get("/").headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void testWrongPrefix() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "" + userJwt);
        mvc.perform(get("/").headers(headers))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testNoHeader() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidExpiredJwt() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + invalidExpiredJwt);
        mvc.perform(get("/").headers(headers))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidEmptyJwt() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + invalidFormatJwt);
        mvc.perform(get("/").headers(headers))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidWrongTokenJwt() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + invalidWrongTokenJwt);
        mvc.perform(get("/").headers(headers))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidWrongUuidJwt() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + invalidWrongUuidJwt);
        mvc.perform(get("/").headers(headers))
                .andExpect(status().isUnauthorized());
    }
}
