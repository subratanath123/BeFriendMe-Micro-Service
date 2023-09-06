package net.befriendme.api.rest.profile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.befriendme.entity.token.TokenResponse;
import net.befriendme.api.common.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static net.befriendme.entity.idp.IdpProvider.GOOGLE;

@RequestMapping("/swagger")
@Controller
public class SwaggerController {

    private Logger log = LoggerFactory.getLogger(SwaggerController.class);

    @Autowired
    private JwtIssuerAuthenticationManagerResolver authenticationManagerResolver;

    @Autowired
    private AuthTokenUtils authTokenUtils;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${befriendme.swaggerUrl}")
    private String swaggerUrl;

    @GetMapping("/auth")
    public String authorzeSwagger(@RequestParam String code,
                                  HttpServletResponse response,
                                  HttpServletRequest request) throws JsonProcessingException {

        log.info("Swagger url authorized");

        TokenResponse tokenResponse = authTokenUtils.getAccessTokenResponse(code, swaggerUrl, GOOGLE);

        log.info("token response {}", tokenResponse.getIdToken());

        String tokenResponseJsonString = getTokenResponseJsonString(tokenResponse);

        redisTemplate.opsForValue().set("user-token-" + tokenResponse.getIdToken(), tokenResponseJsonString);

        log.info("before redirecting to swagger.........{}", tokenResponse.getIdToken());

        // Cookie cookie = new Cookie("Authorization",
                //URLEncoder.encode("Bearer " + tokenResponse.getIdToken(), StandardCharsets.UTF_8));

        log.info("Addinh cookie header.........{}", tokenResponse.getIdToken());

        //response.addCookie(cookie);

        response.setHeader("Set-Cookie", "Authorization=" + "Bearer " + tokenResponse.getIdToken() + "; Path=/; Max-Age=3600;");

        log.info("redirecting to swagger.........");

        return "redirect:/swagger-ui/index.html";
    }

    private String getTokenResponseJsonString(TokenResponse tokenResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(tokenResponse);
    }

}
