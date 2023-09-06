package net.befriendme.api.rest.profile.webclient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import jakarta.ws.rs.BadRequestException;
import net.befriendme.entity.token.TokenRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public abstract class WebClientService {

    public abstract WebClient.Builder getWebClientBuilder();

    public <T> Mono<T> makeRestCall(String url,
                                    MultiValueMap<String, String> headers,
                                    HttpMethod httpMethod,
                                    Supplier<T> fallback,
                                    String body,
                                    Class<T> clazz) {

        return getReactiveCircuitBreaker().run(getWebClientBuilder().build()
                        .method(httpMethod)
                        .uri(url)
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(body))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError,
                                getClientResponseMonoFunction())
                        .bodyToMono(clazz),
                throwable -> Mono.just(fallback.get())
        );
    }

    public <T> Mono<T> makePostRestCall(String url,
                                        Consumer<HttpHeaders> headersConsumer,
                                        MultiValueMap<String, String> formData,
                                        Supplier<T> fallback,
                                        Class<T> clazz) {

        return getReactiveCircuitBreaker().run(
                getWebClientBuilder()
                        .build()
                        .post()
                        .uri(url)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .headers(headersConsumer)
                        .body(BodyInserters.fromFormData(Optional.ofNullable(formData).orElseGet(LinkedMultiValueMap::new)))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError,
                                getClientResponseMonoFunction())
                        .bodyToMono(clazz),
                throwable -> Mono.just(fallback.get())
        );
    }

    public <T> Mono<T> makeGetRestCall(String url,
                                       Consumer<HttpHeaders> headersConsumer,
                                       Supplier<T> fallback,
                                       Class<T> clazz) {

        return getReactiveCircuitBreaker().run(
                getWebClientBuilder()
                        .build()
                        .get()
                        .uri(url)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .headers(headersConsumer)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError,
                                getClientResponseMonoFunction())
                        .bodyToMono(clazz),
                throwable -> Mono.just(fallback.get())
        );
    }

    public <T> Mono<T> makeJsonBodyPostRestCall(String url,
                                                Consumer<HttpHeaders> headersConsumer,
                                                Object requestBodyObject,
                                                Supplier<T> fallback,
                                                Class<T> clazz) {

        return getReactiveCircuitBreaker().run(
                getWebClientBuilder()
                        .build()
                        .post()
                        .uri(url)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .headers(headersConsumer)
                        .bodyValue(requestBodyObject)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, getClientResponseMonoFunction())
                        .bodyToMono(clazz),
                throwable -> Mono.just(fallback.get())
        );
    }

    @NotNull
    private static Function<ClientResponse, Mono<? extends Throwable>> getClientResponseMonoFunction() {
        return response -> switch (response.statusCode().value()) {
            case 400 -> Mono.error(new BadRequestException("bad request made"));
            case 401, 403 -> Mono.error(new Exception("auth error"));
            case 404 -> Mono.error(new Exception("Maybe not an error?"));
            case 500 -> Mono.error(new Exception("server error"));
            default -> Mono.error(new Exception("something went wrong"));
        };
    }

    public abstract ReactiveCircuitBreaker getReactiveCircuitBreaker();
}
