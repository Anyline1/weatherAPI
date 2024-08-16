package ru.anyline.weatherapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;
import java.util.Map;

@Component
public class OpenWeatherMapClient {

    @Value("${api.openweathermap.url}")
    private String openWeatherMapUrl;

    @Value("${api.openweathermap.key}")
    private String openWeatherMapKey;

    public WeatherDataDTO fetchWeatherData(String cityName, LocalDate date) {
        RestTemplate restTemplate = new RestTemplate();
        String formattedDate = date.toString();

        String url = String.format("%s?q=%s&dt=%s&appid=%s", openWeatherMapUrl, cityName, formattedDate, openWeatherMapKey);

        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Error fetching data from OpenWeatherMap: " + e.getMessage());
            return null;
        }

        if (response != null && response.containsKey("main")) {
            Map<String, Object> main = (Map<String, Object>) response.get("main");
            Map<String, Object> wind = (Map<String, Object>) response.get("wind");
            Map<String, Object> clouds = (Map<String, Object>) response.get("clouds");

            WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
            weatherDataDTO.setCityName(cityName);
            weatherDataDTO.setDate(date);

            weatherDataDTO.setTemperature(kelvinToCelsius((Double) main.getOrDefault("temp", 0)));
            weatherDataDTO.setMinTemperature(kelvinToCelsius((Double) main.getOrDefault("temp_min", 0)));
            weatherDataDTO.setMaxTemperature(kelvinToCelsius((Double) main.getOrDefault("temp_max", 0)));
            weatherDataDTO.setHumidity(((Number) main.getOrDefault("humidity", 0)).doubleValue());
            weatherDataDTO.setPressure(((Number) main.getOrDefault("pressure", 0)).doubleValue());

            if (wind != null) {
                weatherDataDTO.setWindSpeed(((Number) wind.getOrDefault("speed", 0)).doubleValue());
            }

            if (clouds != null) {
                weatherDataDTO.setCloudiness(((Number) clouds.getOrDefault("all", 0)).doubleValue());
            }

            return weatherDataDTO;
        }

        return null;
    }
    private double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }
}
