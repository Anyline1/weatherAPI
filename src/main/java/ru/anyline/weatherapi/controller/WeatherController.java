package ru.anyline.weatherapi.controller;

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

    @GetMapping("/forecast")
    public Optional<WeatherDataDTO> getForecastWeatherData(@RequestParam String cityName, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return weatherService.getWeatherData(cityName, localDate);
    }

    @GetMapping("/weatherapi")
    public WeatherDataDTO getWeatherFromWeatherApi(@RequestParam String city,
                                                   @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return weatherApiClient.fetchWeatherData(city, localDate);
    }

    @GetMapping("/openweathermap")
    public WeatherDataDTO getWeatherFromOpenWeatherMap(@RequestParam String city,
                                                       @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return openWeatherMapClient.fetchWeatherData(city, localDate);
    }

    @GetMapping("/all")
    public List<WeatherData> getAllWeatherData() {
        return weatherDataRepository.findAll();
    }
}
