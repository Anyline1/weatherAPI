package ru.anyline.weatherapi.client;

import reactor.core.publisher.Mono;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;

public interface WeatherClient {
    Mono<WeatherDataDTO> fetchWeatherData(String cityName, LocalDate date);
}
