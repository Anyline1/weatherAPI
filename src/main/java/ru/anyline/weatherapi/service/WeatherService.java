package ru.anyline.weatherapi.service;

import reactor.core.publisher.Flux;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;

public interface WeatherService {
    Flux<WeatherDataDTO> getWeatherData(String cityName, LocalDate date);
}
