package ru.anyline.weatherapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class WeatherApiClient {

    @Value("${api.weatherapi.url}")
    private String weatherApiUrl;

    @Value("${api.weatherapi.key}")
    private String weatherApiKey;

    public WeatherDataDTO fetchWeatherData(String cityName, LocalDate date) {
        RestTemplate restTemplate = new RestTemplate();
        String formattedDate = date.toString();

        String url = String.format("%s?q=%s&dt=%s&key=%s", weatherApiUrl, cityName, formattedDate, weatherApiKey);

        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Error fetching data from WeatherAPI: " + e.getMessage());
            return null;
        }

        if (response != null && response.containsKey("forecast")) {
            Map<String, Object> forecast = (Map<String, Object>) response.get("forecast");

            if (forecast != null && forecast.containsKey("forecastday")) {
                List<Map<String, Object>> forecastDays = (List<Map<String, Object>>) forecast.get("forecastday");

                if (forecastDays != null && !forecastDays.isEmpty()) {
                    Map<String, Object> firstDay = forecastDays.get(0);
                    Map<String, Object> day = (Map<String, Object>) firstDay.get("day");

                    WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
                    weatherDataDTO.setCityName(cityName);
                    weatherDataDTO.setDate(date);
                    weatherDataDTO.setTemperature((Double) day.getOrDefault("avgtemp_c", 0.0));
                    weatherDataDTO.setMinTemperature((Double) day.getOrDefault("mintemp_c", 0.0));
                    weatherDataDTO.setMaxTemperature((Double) day.getOrDefault("maxtemp_c", 0.0));
                    weatherDataDTO.setHumidity(((Number) day.getOrDefault("avghumidity", 0)).doubleValue());
                    weatherDataDTO.setWindSpeed(((Number) day.getOrDefault("maxwind_kph", 0)).doubleValue());
                    weatherDataDTO.setCloudiness(((Number) day.getOrDefault("daily_chance_of_rain", 0)).doubleValue());
                    weatherDataDTO.setPressure(((Number) day.getOrDefault("pressure_mb", 0)).doubleValue());

                    return weatherDataDTO;
                }
            }
        }

        return null;
    }
}