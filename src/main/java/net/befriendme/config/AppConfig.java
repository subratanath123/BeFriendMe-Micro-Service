package net.befriendme.config;

import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import net.befriendme.entity.user.redis.RedisBaseEntity;
import org.springframework.cloud.circuitbreaker.resilience4j.*;
import org.springframework.cloud.circuitbreaker.springretry.SpringRetryCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.springretry.SpringRetryConfigBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public RedisTemplate<String, RedisBaseEntity> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RedisBaseEntity> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisBaseEntity.class));

        return redisTemplate;
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient.Builder externalWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultReactiveResilience4JCircuitBreakerFactoryCustomizer() {
        return factory -> factory.configureDefault(
                id -> new
                        Resilience4JConfigBuilder(id)
                        .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
                        .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                        .build()
        );
    }

    @Bean
    public Customizer<Resilience4jBulkheadProvider> defaultResilience4jBulkheadProviderCustomizer() {
        return provider -> provider.configureDefault(
                id -> new Resilience4jBulkheadConfigurationBuilder()
                        .bulkheadConfig(BulkheadConfig.custom().maxConcurrentCalls(10).build())
                        .threadPoolBulkheadConfig(ThreadPoolBulkheadConfig.custom().coreThreadPoolSize(2).maxThreadPoolSize(10).build())
                        .build()
        );
    }

    @Bean
    public Customizer<SpringRetryCircuitBreakerFactory> defaultSpringRetryCircuitBreakerFactoryCustomizer() {
        return factory -> factory.configureDefault(id -> new SpringRetryConfigBuilder(id)
                .retryPolicy(new TimeoutRetryPolicy()).build());
    }

}
