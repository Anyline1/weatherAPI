package ru.anyline.weatherapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;
import java.util.Map;

@Component
public class OpenWeatherMapClient implements WeatherClient {

    private final WebClient webClient;
    private final String weatherApiKey;

    public OpenWeatherMapClient(WebClient.Builder webClientBuilder,
                                @Value("${api.openweathermap.url}") String openWeatherMapUrl,
                                @Value("${api.openweathermap.key}") String weatherApiKey) {
        this.webClient = webClientBuilder.baseUrl(openWeatherMapUrl).build();
        this.weatherApiKey = weatherApiKey;
    }

    @Override
    public Mono<WeatherDataDTO> fetchWeatherData(String cityName, LocalDate date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", cityName)
                        .queryParam("appid", weatherApiKey)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    if (response != null && response.containsKey("main")) {
                        Map<String, Object> main = (Map<String, Object>) response.get("main");
                        Map<String, Object> wind = (Map<String, Object>) response.get("wind");
                        Map<String, Object> clouds = (Map<String, Object>) response.get("clouds");

                        WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
                        weatherDataDTO.setCityName(cityName);
                        weatherDataDTO.setDate(date);
                        weatherDataDTO.setTemperature(convertKelvinToCelsius(((Number) main.getOrDefault("temp", 0)).doubleValue()));
                        weatherDataDTO.setMinTemperature(convertKelvinToCelsius(((Number) main.getOrDefault("temp_min", 0)).doubleValue()));
                        weatherDataDTO.setMaxTemperature(convertKelvinToCelsius(((Number) main.getOrDefault("temp_max", 0)).doubleValue()));
                        weatherDataDTO.setHumidity(((Number) main.getOrDefault("humidity", 0)).doubleValue());
                        weatherDataDTO.setWindSpeed(((Number) wind.getOrDefault("speed", 0)).doubleValue());
                        weatherDataDTO.setCloudiness(((Number) clouds.getOrDefault("all", 0)).doubleValue());
                        weatherDataDTO.setPressure(((Number) main.getOrDefault("pressure", 0)).doubleValue());

                        return Mono.just(weatherDataDTO);
                    }
                    return Mono.empty();
                });
    }
    private double convertKelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }
}
