package net.befriendme.api.common.redis;

import net.befriendme.api.common.service.IdpRegistrationService;
import net.befriendme.entity.idp.IdpProvider;
import net.befriendme.entity.token.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.stereotype.Service;

@Service
public class AppleIdpRegistrationService implements IdpRegistrationService {

    private Logger log = LoggerFactory.getLogger(AppleIdpRegistrationService.class);

    @Override
    public boolean produceNewUserStreamIfNotRegistered(HttpServletRequest request,
                                                       IdpProvider idpProvider,
                                                       TokenResponse token,
                                                       JwtIssuerAuthenticationManagerResolver authenticationManagerResolver) {

        /*
         * Not implemented yet
         * TODO:: Implement apple user entry
         */
        return false;
    }
}
