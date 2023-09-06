package net.befriendme.api.common.redis;

import net.befriendme.api.common.redis.stream.RedisStreamPublisherService;
import net.befriendme.api.common.service.IdpRegistrationService;
import net.befriendme.entity.idp.IdpProvider;
import net.befriendme.entity.token.TokenResponse;
import net.befriendme.entity.idp.IdpUser;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class GoogleIdpRegistrationService implements IdpRegistrationService {

    private Logger log = LoggerFactory.getLogger(GoogleIdpRegistrationService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisStreamPublisherService redisStreamPublisherService;

    @Override
    public boolean produceNewUserStreamIfNotRegistered(HttpServletRequest request,
                                                       IdpProvider idpProvider,
                                                       TokenResponse token,
                                                       JwtIssuerAuthenticationManagerResolver authenticationManagerResolver) {
        Authentication authentication = authenticationManagerResolver
                .resolve(request)
                .authenticate(new BearerTokenAuthenticationToken(token.getIdToken()));

        authentication.getPrincipal();

        // For google this is ok.
        String email = (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get("email");
        String profilePictureUrl = (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get("picture");

        //Will manage separate
        if (!TRUE.equals(redisTemplate.opsForHash().hasKey("user-profile-" + email, "data"))) {

            IdpUser idpUser = new IdpUser();
            idpUser.setIdpProvider(idpProvider);
            idpUser.setTokenInfo(token);
            idpUser.setEmail(email);
            idpUser.setProfilePictureUrl(profilePictureUrl);

            RecordId recordId = produceNewUserStream(idpUser);

            log.info("Publishing new google user with recordId = " + recordId);

            return true;
        }

        return false;
    }

    private RecordId produceNewUserStream(IdpUser idpUser) {
        return redisStreamPublisherService.produce("new-user", idpUser);
    }

}
