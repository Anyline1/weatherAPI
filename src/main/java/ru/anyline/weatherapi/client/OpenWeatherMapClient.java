package ru.anyline.weatherapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public class OpenWeatherMapClient {

    @Value("${api.openweathermap.url}")
    private String openWeatherMapUrl;

    @Value("${api.openweathermap.key}")
    private String openWeatherMapKey;

    public WeatherDataDTO fetchWeatherData(String cityName, LocalDate date) {
        String url = String.format("%s?q=%s&dt=%s&appid=%s", openWeatherMapUrl, cityName, date, openWeatherMapKey);
        RestTemplate restTemplate = new RestTemplate();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null) {
                return buildWeatherDataDTO(cityName, date, response);
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error fetching data from OpenWeatherMap: " + e.getMessage());
        }

        return null;
    }

    private WeatherDataDTO buildWeatherDataDTO(String cityName, LocalDate date, Map<String, Object> response) {
        Map<String, Object> main = (Map<String, Object>) response.getOrDefault("main", Collections.emptyMap());

        double temperature = kelvinToCelsius((Double) main.getOrDefault("temp", 0.0));
        double minTemperature = kelvinToCelsius((Double) main.getOrDefault("temp_min", 0.0));
        double maxTemperature = kelvinToCelsius((Double) main.getOrDefault("temp_max", 0.0));
        double humidity = ((Number) main.getOrDefault("humidity", 0)).doubleValue();
        double pressure = ((Number) main.getOrDefault("pressure", 0)).doubleValue();

        double windSpeed = Optional.ofNullable((Map<String, Object>) response.get("wind"))
                .map(wind -> ((Number) wind.getOrDefault("speed", 0)).doubleValue())
                .orElse(0.0);

        double cloudiness = Optional.ofNullable((Map<String, Object>) response.get("clouds"))
                .map(clouds -> ((Number) clouds.getOrDefault("all", 0)).doubleValue())
                .orElse(0.0);


        return WeatherDataDTO.builder()
                .cityName(cityName)
                .date(date)
                .temperature(temperature)
                .minTemperature(minTemperature)
                .maxTemperature(maxTemperature)
                .humidity(humidity)
                .pressure(pressure)
                .windSpeed(windSpeed)
                .cloudiness(cloudiness)
                .build();
    }

    private double kelvinToCelsius(double kelvin) {
        return Math.round((kelvin - 273.15) * 10) / 10.0;
    }
}
