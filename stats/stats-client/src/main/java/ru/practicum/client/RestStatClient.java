package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.net.URI;
import java.util.List;

@Service
public class RestStatClient{
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    @Value("${stats.serviceName}")
    private String serviceName;

    public RestStatClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;

        restClient = RestClient.builder()
                .build();
    }

    public void saveHit(HitDto hitDto) {
        restClient.post()
                .uri(makeUri("/hit"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .onStatus(
                        status -> status != HttpStatus.CREATED,
                        (request, response) -> {
                            throw new RuntimeException("Не удалось сохранить Hit: " + response.getStatusCode());
                        }
                )
                .toBodilessEntity();

    }

    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        String url = UriComponentsBuilder.fromUri(makeUri("/stats"))
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris != null && !uris.isEmpty() ? String.join(",", uris) : null)
                .queryParam("unique", unique)
                .build()
                .toUriString();

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<StatsDto>>() {
                });
    }

    private URI makeUri(String path) {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        ServiceInstance instance = retryTemplate.execute(cxt -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(serviceName)
                    .getFirst();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
