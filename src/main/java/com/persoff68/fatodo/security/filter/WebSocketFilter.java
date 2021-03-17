package com.persoff68.fatodo.security.filter;

import com.persoff68.fatodo.config.AppProperties;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketFilter implements ChannelInterceptor {

    private final AppProperties appProperties;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwt = getJwtFromAccessor(accessor);
            boolean hasJwt = StringUtils.hasText(jwt) && jwtTokenProvider.validateJwt(jwt);
            if (hasJwt) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenProvider.getAuthenticationFromJwt(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                accessor.setUser(authentication);
            }
        }
        return message;
    }

    private String getJwtFromAccessor(StompHeaderAccessor accessor) {
        String authHeader = appProperties.getAuth().getAuthorizationHeader();
        String authPrefix = appProperties.getAuth().getAuthorizationPrefix();

        String jwt = null;
        List<String> authorization = accessor.getNativeHeader(authHeader);
        if (authorization != null && !authorization.isEmpty()) {
            String bearerToken = authorization.get(0);
            boolean hasToken = StringUtils.hasText(bearerToken) && bearerToken.startsWith(authPrefix);
            jwt = hasToken ? bearerToken.substring(authPrefix.length()) : null;
        }
        return jwt;
    }

}
