package net.befriendme.api.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.befriendme.entity.token.TokenResponse;
import net.befriendme.api.common.utils.AuthTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenRefresherService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AuthTokenUtils authTokenUtils;

    public String validateAndGetAccessTokenIfExpired(String userToken) throws JsonProcessingException {

        String accessTokenPayload = redisTemplate.opsForValue().get("user-token-" + userToken);

        if (accessTokenPayload == null) {
            return userToken;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TokenResponse existingTokenPayload = mapper.readValue(accessTokenPayload, TokenResponse.class);

        if (existingTokenPayload.isTokenExpired()) {

            TokenResponse newTokenResponse = authTokenUtils.getAccessTokenWithRefreshToken(
                    existingTokenPayload.getRefreshToken(),
                    existingTokenPayload.getIdpProvider()
            );

            String newTokenResponseJson = mapper.writeValueAsString(newTokenResponse);

            redisTemplate.delete(userToken);

            redisTemplate.opsForValue().set("user-token-" + newTokenResponse.getIdToken(), newTokenResponseJson);

            return newTokenResponse.getIdToken();
        }

        return userToken;
    }
}
