package ru.anyline.weatherapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.anyline.weatherapi.entity.WeatherData;

public interface WService {

    WeatherData getWeather(String city) throws JsonProcessingException;
}
