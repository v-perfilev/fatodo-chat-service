package com.persoff68.fatodo.security.jwt;

import com.persoff68.fatodo.config.AppProperties;
import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.security.details.CustomUserDetails;
import com.persoff68.fatodo.security.util.SecurityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String USERNAME_KEY = "username";
    private static final String AUTHORITY_KEY = "authorities";

    private final AppProperties appProperties;

    public UsernamePasswordAuthenticationToken getAuthenticationFromJwt(String jwt) {
        Claims claims = getClaimsFromJwt(jwt);
        UUID id = SecurityUtils.getUuidFromString(claims.getSubject());
        String username = claims.get(USERNAME_KEY).toString();
        List<? extends GrantedAuthority> authorityList =
                Arrays.stream(claims.get(AUTHORITY_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        CustomUserDetails userDetails = new CustomUserDetails(id, username, "", authorityList);
        return new UsernamePasswordAuthenticationToken(userDetails, jwt, authorityList);
    }

    public String createUserJwt(UUID id, User user) {
        long tokenExpirationSec = appProperties.getAuth().getTokenExpirationSec();

        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date nowDate = new Date();
        Date expirationDate = new Date(nowDate.getTime()
                + TimeUnit.SECONDS.toMillis(tokenExpirationSec));

        JwtParams params = JwtParams.builder()
                .id(id)
                .username(user.getUsername())
                .authorities(authorities)
                .nowDate(nowDate)
                .expirationDate(expirationDate)
                .build();

        return buildJwt(params);
    }

    public String createSystemJwt() {
        Date nowDate = new Date();
        Date expirationDate = new Date(nowDate.getTime()
                + TimeUnit.SECONDS.toMillis(AppConstants.SYSTEM_TOKEN_EXPIRATION_SEC));

        JwtParams params = JwtParams.builder()
                .id(AppConstants.SYSTEM_ID)
                .username(AppConstants.SYSTEM_USERNAME)
                .authorities(AppConstants.SYSTEM_AUTHORITY)
                .nowDate(nowDate)
                .expirationDate(expirationDate)
                .build();

        return buildJwt(params);
    }

    public boolean validateJwt(String jwt) {
        try {
            String tokenSecret = appProperties.getAuth().getTokenSecret();
            String id = Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(jwt).getBody().getSubject();
            if (SecurityUtils.getUuidFromString(id) == null) {
                throw new IllegalArgumentException();
            }
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string not valid");
        }
        return false;
    }

    private Claims getClaimsFromJwt(String jwt) {
        String tokenSecret = appProperties.getAuth().getTokenSecret();
        return Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(jwt)
                .getBody();
    }

    private String buildJwt(JwtParams params) {
        String tokenSecret = appProperties.getAuth().getTokenSecret();
        return Jwts.builder()
                .setSubject(params.getId().toString())
                .claim(USERNAME_KEY, params.getUsername())
                .claim(AUTHORITY_KEY, params.getAuthorities())
                .setIssuedAt(params.getNowDate())
                .setExpiration(params.getExpirationDate())
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    @Getter
    @Builder
    private static class JwtParams {
        private final UUID id;
        private final String username;
        private final String authorities;
        private final Date nowDate;
        private final Date expirationDate;
    }

}
