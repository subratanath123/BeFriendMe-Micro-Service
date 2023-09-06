package net.befriendme.api.rest.profile.webclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

//Exception Handling resource
// https://mohosinmiah1610.medium.com/error-handling-with-webclient-in-spring-boot-e604733071e0
@Component
public class LoadBalancedWebClientService extends WebClientService {

    @Qualifier("loadBalancedWebClientBuilder")
    @Autowired
    @LoadBalanced
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory;

    @Override
    public WebClient.Builder getWebClientBuilder() {
        return webClientBuilder;
    }

    @Override
    public ReactiveCircuitBreaker getReactiveCircuitBreaker() {
        return circuitBreakerFactory.create("befriend-me-circuit-breaker");
    }
}
