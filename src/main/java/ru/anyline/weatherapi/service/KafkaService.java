package ru.anyline.weatherapi.service;

import ru.anyline.weatherapi.WeatherData;

public interface KafkaService {

    void sendWeatherData(String city, WeatherData weatherData);
}
