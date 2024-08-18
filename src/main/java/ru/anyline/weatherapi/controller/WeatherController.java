package ru.anyline.weatherapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.anyline.weatherapi.client.OpenWeatherMapClient;
import ru.anyline.weatherapi.client.WeatherApiClient;
import ru.anyline.weatherapi.model.WeatherData;
import ru.anyline.weatherapi.model.WeatherDataDTO;
import ru.anyline.weatherapi.repository.WeatherDataRepository;
import ru.anyline.weatherapi.service.WeatherService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Weather API", description = "API for retrieving weather data from various sources")
public class WeatherController {

    private final WeatherService weatherService;
    private final OpenWeatherMapClient openWeatherMapClient;
    private final WeatherDataRepository weatherDataRepository;
    private final WeatherApiClient weatherApiClient;

    public WeatherController(WeatherService weatherService, OpenWeatherMapClient openWeatherMapClient, WeatherDataRepository weatherDataRepository, WeatherApiClient weatherApiClient) {
        this.weatherService = weatherService;
        this.openWeatherMapClient = openWeatherMapClient;
        this.weatherDataRepository = weatherDataRepository;
        this.weatherApiClient = weatherApiClient;
    }
    @Operation(summary = "Forecast API", description = "Forecast weather API method returns, upto next 14 day weather forecast and weather alert as json.")
    @GetMapping("/forecast")
    public Optional<WeatherDataDTO> getForecastWeatherData(@RequestParam String cityName, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return weatherService.getWeatherData(cityName, localDate);
    }
    @Operation(summary = "Realtime API from weatherapi", description = "Current weather or realtime weather API method allows a user to get up to date current weather information in json and xml. The data is returned as a Current Object.")
    @GetMapping("/weatherapi")
    public WeatherDataDTO getWeatherFromWeatherApi(@RequestParam String city,
                                                   @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return weatherApiClient.fetchWeatherData(city, localDate);
    }
    @Operation(summary = "Realtime API from openweathermap", description = "Current weather or realtime weather API method allows a user to get up to date current weather information in json and xml. The data is returned as a Current Object.")
    @GetMapping("/openweathermap")
    public WeatherDataDTO getWeatherFromOpenWeatherMap(@RequestParam String city,
                                                       @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return openWeatherMapClient.fetchWeatherData(city, localDate);
    }
    @Operation(summary = "Get List of saved data", description = "List of saved data")
    @GetMapping("/all")
    public List<WeatherData> getAllWeatherData() {
        return weatherDataRepository.findAll();
    }
}
