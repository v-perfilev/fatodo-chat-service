package com.persoff68.fatodo.security;

import com.persoff68.fatodo.config.AppProperties;
import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.security.details.CustomUserDetails;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    JwtTokenProvider jwtTokenProvider;
    AppProperties appProperties;
    AppProperties.Auth auth;


    @BeforeEach
    void setup() {
        appProperties = Mockito.mock(AppProperties.class);
        auth = Mockito.mock(AppProperties.Auth.class);
        Mockito.when(appProperties.getAuth()).thenReturn(auth);
        Mockito.when(auth.getTokenSecret()).thenReturn("4323B829EF1EEF14E324241FB72E6");
        jwtTokenProvider = new JwtTokenProvider(appProperties);
    }

    @Test
    void testGetAuthenticationFromJwt() {
        String jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiNDQ2Y2I5OS01ZGQ1LTQ4YTgtOWNjMS1kMWI4OTc5NzY4ZmMiLCJ1c2VybmFtZSI6InRlc3RfdGVzdCIsImF1dGhvcml0aWVzIjoiUk9MRV9URVNUIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.AxcHnejWG4Y_edm_ymjO6U92UPKoZTn_a5kLLv4j_M4bvGkCOmMigLET6a9F4DpbVW2zUlnXNyvVY_KpxadEQg";
        UUID id = UUID.fromString("b446cb99-5dd5-48a8-9cc1-d1b8979768fc");
        String username = "test_test";
        List<? extends GrantedAuthority> authorityList =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEST"));
        CustomUserDetails userDetails = new CustomUserDetails(id, username, "", authorityList);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, jwt, authorityList);
        Authentication resultAuthentication = jwtTokenProvider.getAuthenticationFromJwt(jwt);
        assertThat(resultAuthentication).isEqualTo(authentication);
    }

    @Test
    void testCreateUserJwtAndValidateJwt() {
        Mockito.when(auth.getTokenExpirationSec()).thenReturn(100L);
        UUID id = UUID.randomUUID();
        User user = new User("test_user", "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEST")));
        String jwt = jwtTokenProvider.createUserJwt(id, user);
        boolean isValid = jwtTokenProvider.validateJwt(jwt);
        assertThat(isValid).isTrue();
    }

    @Test
    void testCreateSystemJwtAndValidateJwt() {
        String jwt = jwtTokenProvider.createSystemJwt();
        Authentication authentication = jwtTokenProvider.getAuthenticationFromJwt(jwt);
        boolean isValid = jwtTokenProvider.validateJwt(jwt);
        boolean hasSystemAuthority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(s -> s.equals(AppConstants.SYSTEM_AUTHORITY));
        assertThat(isValid).isTrue();
        assertThat(hasSystemAuthority).isTrue();
    }

}
