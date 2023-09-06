package net.befriendme.api.rest.profile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.befriendme.api.common.redis.AppleIdpRegistrationService;
import net.befriendme.api.common.redis.GoogleIdpRegistrationService;
import net.befriendme.api.common.utils.AuthTokenUtils;
import net.befriendme.entity.token.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.befriendme.entity.idp.IdpProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class AuthController {

    private Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtIssuerAuthenticationManagerResolver authenticationManagerResolver;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GoogleIdpRegistrationService googleUserProfileService;

    @Autowired
    private AppleIdpRegistrationService appleIdpRegistrationService;

    @Autowired
    private AuthTokenUtils authTokenUtils;

    @GetMapping("/auth")
    public TokenResponse getUser(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        TokenResponse tokenResponse = authTokenUtils.getAccessTokenResponse(code, IdpProvider.BEFRIENDME);

        publishUserTokenToRedisStore(tokenResponse);

        response.addHeader("Set-Cookie", "Authorization=Bearer " + tokenResponse.getAccessToken() + "; SameSite=None; Secure");

        return tokenResponse;
    }

    //    https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=288446859744-4g08lrmcci6gr5ujs5bk2gfcod56sp9s.apps.googleusercontent.com&redirect_uri=http://localhost:8000/gauth&scope=profile%20email%20openid
//    https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=288446859744-6gn3vin68o0l5f3mvl86cml5ec2c3d17.apps.googleusercontent.com&redirect_uri=http://localhost:8001/gauth&scope=profile%20email%20openid
    @GetMapping("/gauth")
    public TokenResponse getGoogleUser(@RequestParam String code, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {

        TokenResponse tokenResponse = authTokenUtils.getAccessTokenResponse(code, IdpProvider.GOOGLE);

        publishUserTokenToRedisStore(tokenResponse);

        googleUserProfileService.produceNewUserStreamIfNotRegistered(request, IdpProvider.GOOGLE, tokenResponse, authenticationManagerResolver);

        response.addHeader("Set-Cookie", "Authorization=Bearer " + tokenResponse.getIdToken() + "; SameSite=None; Secure");

        return tokenResponse;
    }

    // https://appleid.apple.com/auth/authorize?response_type=code&client_id=com.befriendme.authorizationserver&redirect_uri=https://befriendme.me/appleauth&&scope=openid%20name%20email&response_mode=form_post
    @PostMapping("/appleauth")
    public TokenResponse getAppleUser(@RequestParam String code, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {

        TokenResponse tokenResponse = authTokenUtils.getAccessTokenResponse(code, IdpProvider.APPLE);

        publishUserTokenToRedisStore(tokenResponse);

        appleIdpRegistrationService.produceNewUserStreamIfNotRegistered(request, IdpProvider.APPLE, tokenResponse, authenticationManagerResolver);

        response.addHeader("Set-Cookie", "Authorization=Bearer " + tokenResponse.getAccessToken() + "; SameSite=None; Secure");

        return tokenResponse;
    }

    private void publishUserTokenToRedisStore(TokenResponse tokenResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String tokenResponseJsonString = mapper.writeValueAsString(tokenResponse);

        redisTemplate.opsForValue().set("user-token-" + tokenResponse.getIdToken(), tokenResponseJsonString, 5, TimeUnit.DAYS);
    }

}
