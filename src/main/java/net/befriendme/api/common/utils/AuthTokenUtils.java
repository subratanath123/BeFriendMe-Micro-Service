package net.befriendme.api.common.utils;

import net.befriendme.entity.idp.*;
import net.befriendme.entity.token.TokenResponse;
import net.befriendme.api.rest.profile.webclient.ExternalWebClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;
import java.util.function.Consumer;

@Component
public class AuthTokenUtils {

    @Autowired
    private BeFriendMeIdpConfig beFriendMeIdpConfig;

    @Autowired
    private GoogleIdpConfig googleIdpConfig;

    @Autowired
    private AppleIdpConfig appleIdpConfig;

    @Autowired
    private ExternalWebClientService externalWebClientService;

    public TokenResponse getAccessTokenResponse(String code,
                                                String redirectUrl,
                                                IdpProvider idpProvider) {
        return getTokenResponse(code, "authorization_code", redirectUrl, getIdpConfig(idpProvider), false);
    }

    public TokenResponse getAccessTokenResponse(String code,
                                                IdpProvider idpProvider) {
        IdpConfig idpConfig = getIdpConfig(idpProvider);

        return getTokenResponse(code, "authorization_code", idpConfig.getRedirectUrl(), getIdpConfig(idpProvider), false);
    }

    public TokenResponse getAccessTokenWithRefreshToken(String code,
                                                        IdpProvider idpProvider) {

        IdpConfig idpConfig = getIdpConfig(idpProvider);

        return getTokenResponse(code, "refresh_token", idpConfig.getRedirectUrl(), idpConfig, true);
    }

    private TokenResponse getTokenResponse(String code,
                                           String grantType,
                                           String redirectUrl,
                                           IdpConfig idpConfig,
                                           Boolean isForRefreshToken) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", grantType);
        formData.add(isForRefreshToken ? "refresh_token" : "code", code);
        formData.add("redirect_uri", redirectUrl);

        Consumer<HttpHeaders> headersConsumer = headers -> {
            String encodedCredentials = Base64.getEncoder()
                    .encodeToString(idpConfig
                            .getClientId()
                            .concat(":")
                            .concat(idpConfig.getClientSecret())
                            .getBytes()
                    );

            headers.setBasicAuth(encodedCredentials); // Set Basic Authentication header

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        };

        TokenResponse tokenResponse = externalWebClientService.makePostRestCall(
                idpConfig.getTokenUrl(),
                headersConsumer,
                formData,
                () -> {
                    throw new RuntimeException();
                },
                TokenResponse.class
        ).block();

        if (tokenResponse != null) {
            tokenResponse.setIssuedTime();
            tokenResponse.setIdpProvider(idpConfig.getIdpProvider());
        }

        return tokenResponse;
    }

    private IdpConfig getIdpConfig(IdpProvider idpProvider) {

        if (beFriendMeIdpConfig.getIdpProvider().equals(idpProvider)) {
            return beFriendMeIdpConfig;
        }
        if (googleIdpConfig.getIdpProvider().equals(idpProvider)) {
            return googleIdpConfig;
        }
        if (appleIdpConfig.getIdpProvider().equals(idpProvider)) {
            return appleIdpConfig;
        }

        throw new RuntimeException("Unsupported idp Provider Passed");
    }

}
