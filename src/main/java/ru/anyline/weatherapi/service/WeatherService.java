package ru.anyline.weatherapi.service;

import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherService {
    Optional<WeatherDataDTO> getWeatherData(String cityName, LocalDate date);

}
