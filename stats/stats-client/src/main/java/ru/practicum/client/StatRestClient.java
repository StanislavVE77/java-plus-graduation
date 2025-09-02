package ru.practicum.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.util.List;

@Component
public class StatRestClient {

    private final RestClient restClient;

    public StatRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void saveHit(HitDto hitDto) {
        restClient.post()
                .uri("/hit")
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
        String url = UriComponentsBuilder.fromHttpUrl("http://stats-server:9090/stats")
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
}
