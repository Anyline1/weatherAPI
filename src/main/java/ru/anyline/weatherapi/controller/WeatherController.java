package ru.anyline.weatherapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anyline.weatherapi.model.WeatherData;
import ru.anyline.weatherapi.model.WeatherDataDTO;
import ru.anyline.weatherapi.repository.WeatherDataRepository;
import ru.anyline.weatherapi.service.WeatherService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherDataRepository weatherDataRepository;

    public WeatherController(WeatherService weatherService, WeatherDataRepository weatherDataRepository) {
        this.weatherService = weatherService;
        this.weatherDataRepository = weatherDataRepository;
    }

    @GetMapping
    public Optional<WeatherDataDTO> getWeatherData(@RequestParam String cityName, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return weatherService.getWeatherData(cityName, localDate);
    }

    @GetMapping("/all")
    public List<WeatherData> getAllWeatherData() {
        return weatherDataRepository.findAll();
    }
}
