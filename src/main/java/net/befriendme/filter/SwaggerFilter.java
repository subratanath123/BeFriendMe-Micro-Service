package net.befriendme.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.befriendme.entity.token.TokenResponse;
import net.befriendme.api.common.request.CustomHeaderRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SwaggerFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${befriendme.swaggerUrl}")
    private String swaggerUrl;

    Logger logger = LoggerFactory.getLogger(SwaggerFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {

        CustomHeaderRequestWrapper customHeaderRequest = new CustomHeaderRequestWrapper(request);

        String requestUrl = String.valueOf(request.getRequestURL());
        logger.debug("Request URL :" + requestUrl);

        //For Web Client swagger
        if (!requestUrl.contains("/swagger/auth") && (requestUrl.contains("swagger") || requestUrl.contains("api-docs"))) {
            String accessToken = null;

            if (request.getHeader("Cookie") != null) {
                List<String> cookieHeaders = Arrays.asList(request.getHeader("Cookie").split("; "));

                for (String cookieHeader : cookieHeaders) {
                    if (cookieHeader.startsWith("Authorization=")) {
                        accessToken = cookieHeader.substring("Authorization=Bearer ".length());
                        break;
                    }
                }
            }

            if (accessToken != null && accessToken.length() > 0) {
                TokenResponse tokenResponse = getTokenResponse(accessToken);
/*
                if (!List.of("shuvra.dev9@gmail.com").contains(tokenResponse.getEmail())) {
                    throw new RuntimeException();
                }
*/
                customHeaderRequest.addHeader("Authorization", "Bearer " + accessToken);

            } else {
                response.sendRedirect("https://accounts.google.com/o/oauth2/auth?response_type=code" +
                        "&client_id=288446859744-6gn3vin68o0l5f3mvl86cml5ec2c3d17.apps.googleusercontent.com" +
                        "&redirect_uri=" + swaggerUrl +
                        "&scope=profile%20email%20openid");
                return;
            }
        }

        filterChain.doFilter(customHeaderRequest, response);
    }

    private TokenResponse getTokenResponse(String accessToken) throws JsonProcessingException {
        String accessTokenPayload = redisTemplate.opsForValue().get("user-token-" + accessToken);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(accessTokenPayload, TokenResponse.class);
    }
}
