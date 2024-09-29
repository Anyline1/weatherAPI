package ru.anyline.weatherapi.service;

import ru.anyline.weatherapi.entity.WeatherData;

public interface KafkaService {

    void sendWeatherData(String city, WeatherData weatherData);
}
