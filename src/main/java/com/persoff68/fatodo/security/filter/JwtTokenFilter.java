package com.persoff68.fatodo.security.filter;

import com.persoff68.fatodo.config.AppProperties;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        boolean hasJwt = StringUtils.hasText(jwt) && jwtTokenProvider.validateJwt(jwt);

        if (hasJwt) {
            UsernamePasswordAuthenticationToken authentication = jwtTokenProvider.getAuthenticationFromJwt(jwt);
            WebAuthenticationDetails authenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);
            authentication.setDetails(authenticationDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = appProperties.getAuth().getAuthorizationHeader();
        String authPrefix = appProperties.getAuth().getAuthorizationPrefix();

        String jwt = null;
        List<String> headerList = Collections.list(request.getHeaderNames()).stream()
                .map(String::toLowerCase).toList();
        boolean hasHeader = headerList.contains(authHeader.toLowerCase());
        if (hasHeader) {
            String bearerToken = request.getHeader(authHeader);
            boolean hasToken = StringUtils.hasText(bearerToken) && bearerToken.startsWith(authPrefix);
            jwt = hasToken ? bearerToken.substring(authPrefix.length()) : null;
        }
        return jwt;
    }

}
