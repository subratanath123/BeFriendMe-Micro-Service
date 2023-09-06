package net.befriendme.api.common.service;

import net.befriendme.api.rest.profile.webclient.LoadBalancedWebClientService;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class MicroserviceInvokeService {

    @Autowired
    private LoadBalancedWebClientService webClientService;

    public <T> T invokeTargetMicroService(T payLoadObject, Class<T> clazz, String serviceUrl) {

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        final String token = getAccessTokenFromAuthentication(authentication);

        Consumer<HttpHeaders> headersConsumer = headers -> {
            assert token != null;
            headers.setBearerAuth(token); // Set Basic Authentication header
            headers.setContentType(MediaType.APPLICATION_JSON);
        };

        T finalPayloadObject = payLoadObject;

        payLoadObject = webClientService.makeJsonBodyPostRestCall(
                serviceUrl,
                headersConsumer,
                payLoadObject,
                () -> finalPayloadObject,
                clazz
        ).block();

        return payLoadObject;
    }

    @Nullable
    private static String getAccessTokenFromAuthentication(JwtAuthenticationToken authentication) {
        return authentication.getToken().getTokenValue();
    }
}
