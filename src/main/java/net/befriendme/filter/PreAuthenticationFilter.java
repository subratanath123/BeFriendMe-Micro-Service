package net.befriendme.filter;

import net.befriendme.api.common.redis.TokenRefresherService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class PreAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenRefresherService tokenRefresherService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer")) {

            String accessToken = authorization.substring(7);

            String validatedAccessToken = tokenRefresherService.validateAndGetAccessTokenIfExpired(accessToken);

            if (!accessToken.equals(validatedAccessToken)) {
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(new BearerTokenAuthenticationToken(validatedAccessToken));

                response.setHeader("Authorization", "Bearer ".concat(validatedAccessToken));
            }
        }

        filterChain.doFilter(request, response);

    }

}
