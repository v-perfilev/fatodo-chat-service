package com.persoff68.fatodo.security;

import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.security.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SecurityUtils.class)
class SecurityUtilsIT {

    @Test
    @WithAnonymousUser
    void testGetCurrentId_ifAnonymous() {
        Optional<UUID> idOptional = SecurityUtils.getCurrentId();
        assertThat(idOptional).isNotPresent();
    }

    @Test
    @WithCustomSecurityContext
    void testGetCurrentId_ifAuthorized() {
        Optional<UUID> idOptional = SecurityUtils.getCurrentId();
        assertThat(idOptional)
                .isPresent()
                .contains(UUID.fromString("41789d72-3fa5-4019-8205-ccf44213b322"));
    }

    @Test
    @WithAnonymousUser
    void testGetCurrentUsername_ifAnonymous() {
        Optional<String> usernameOptional = SecurityUtils.getCurrentUsername();
        assertThat(usernameOptional).isNotPresent();
    }

    @Test
    @WithCustomSecurityContext
    void testGetCurrentUsername_ifAuthorized() {
        Optional<String> usernameOptional = SecurityUtils.getCurrentUsername();
        assertThat(usernameOptional)
                .isPresent()
                .contains("test_username");
    }

    @Test
    @WithAnonymousUser
    void testGetCurrentJwt_ifAnonymous() {
        Optional<String> jwtOptional = SecurityUtils.getCurrentJwt();
        assertThat(jwtOptional).isNotPresent();
    }

    @Test
    @WithCustomSecurityContext
    void testGetCurrentJwt_ifAuthorized() {
        Optional<String> jwtOptional = SecurityUtils.getCurrentJwt();
        assertThat(jwtOptional)
                .isPresent()
                .contains("test_jwt");
    }

    @Test
    @WithAnonymousUser
    void testGetCurrentAuthoritySet_ifAnonymous() {
        Optional<Set<GrantedAuthority>> authoritySetOptional = SecurityUtils.getCurrentAuthoritySet();
        assertThat(authoritySetOptional).isNotPresent();
    }

    @Test
    @WithCustomSecurityContext
    void testGetCurrentAuthoritySet_ifAuthorized() {
        Optional<Set<GrantedAuthority>> authoritySetOptional = SecurityUtils.getCurrentAuthoritySet();
        assertThat(authoritySetOptional).isPresent();
        assertThat(authoritySetOptional.get()).contains(new SimpleGrantedAuthority("ROLE_TEST"));
        assertThat(authoritySetOptional.get()).hasSize(1);
    }

}
