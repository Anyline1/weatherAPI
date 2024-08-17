package ru.anyline.weatherapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class WeatherApiClient implements WeatherClient {

    private final WebClient webClient;
    private final String weatherApiKey;

    public WeatherApiClient(WebClient.Builder webClientBuilder,
                            @Value("${api.weatherapi.url}") String weatherApiUrl,
                            @Value("${api.weatherapi.key}") String weatherApiKey) {
        this.webClient = webClientBuilder.baseUrl(weatherApiUrl).build();
        this.weatherApiKey = weatherApiKey;
    }

    @Override
    public Mono<WeatherDataDTO> fetchWeatherData(String cityName, LocalDate date) {
        String formattedDate = date.toString();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", cityName)
                        .queryParam("dt", formattedDate)
                        .queryParam("key", weatherApiKey)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    if (response != null && response.containsKey("forecast")) {
                        Map<String, Object> forecast = (Map<String, Object>) response.get("forecast");
                        List<Map<String, Object>> forecastDays = (List<Map<String, Object>>) forecast.get("forecastday");
                        Map<String, Object> firstDay = forecastDays.get(0);
                        Map<String, Object> day = (Map<String, Object>) firstDay.get("day");

                        WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
                        weatherDataDTO.setCityName(cityName);
                        weatherDataDTO.setDate(date);
                        weatherDataDTO.setTemperature(((Number) day.getOrDefault("avgtemp_c", 0)).doubleValue());
                        weatherDataDTO.setMinTemperature(((Number) day.getOrDefault("mintemp_c", 0)).doubleValue());
                        weatherDataDTO.setMaxTemperature(((Number) day.getOrDefault("maxtemp_c", 0)).doubleValue());
                        weatherDataDTO.setHumidity(((Number) day.getOrDefault("avghumidity", 0)).doubleValue());
                        weatherDataDTO.setWindSpeed(((Number) day.getOrDefault("maxwind_kph", 0)).doubleValue());
                        weatherDataDTO.setCloudiness(((Number) day.getOrDefault("daily_chance_of_rain", 0)).doubleValue());
                        weatherDataDTO.setPressure(((Number) day.getOrDefault("pressure_mb", 0)).doubleValue());

                        return Mono.just(weatherDataDTO);
                    }
                    return Mono.empty();
                });
    }
}
