package ru.anyline.weatherapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class WeatherApiClient {

    @Value("${api.weatherapi.url}")
    private String weatherApiUrl;

    @Value("${api.weatherapi.key}")
    private String weatherApiKey;

    public WeatherDataDTO fetchWeatherData(String cityName, LocalDate date) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?q=%s&dt=%s&key=%s", weatherApiUrl, cityName, date, weatherApiKey);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                Map<String, Object> day = extractDayData(response);

                if (day != null) {
                    return mapToWeatherDataDTO(cityName, date, day);
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error fetching data from WeatherAPI: " + e.getMessage());
        }

        return null;
    }

    private Map<String, Object> extractDayData(Map<String, Object> response) {
        return Optional.ofNullable((Map<String, Object>) response.get("forecast"))
                .map(forecast -> (List<Map<String, Object>>) forecast.get("forecastday"))
                .filter(forecastDays -> !forecastDays.isEmpty())
                .map(forecastDays -> (Map<String, Object>) forecastDays.get(0).get("day"))
                .orElse(null);
    }

    private WeatherDataDTO mapToWeatherDataDTO(String cityName, LocalDate date, Map<String, Object> day) {
        return WeatherDataDTO.builder()
                .cityName(cityName)
                .date(date)
                .temperature((Double) day.getOrDefault("avgtemp_c", 0.0))
                .minTemperature((Double) day.getOrDefault("mintemp_c", 0.0))
                .maxTemperature((Double) day.getOrDefault("maxtemp_c", 0.0))
                .humidity(((Number) day.getOrDefault("avghumidity", 0)).doubleValue())
                .windSpeed(((Number) day.getOrDefault("maxwind_kph", 0)).doubleValue())
                .cloudiness(((Number) day.getOrDefault("daily_chance_of_rain", 0)).doubleValue())
                .pressure(((Number) day.getOrDefault("pressure_mb", 0)).doubleValue())
                .build();
    }
}
