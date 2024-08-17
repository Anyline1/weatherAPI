package ru.anyline.weatherapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.anyline.weatherapi.model.WeatherData;
import ru.anyline.weatherapi.repository.WeatherDataRepository;

import java.time.LocalDate;

@RestController
@RequestMapping("/")
public class WeatherController {

    private final WeatherDataRepository weatherDataRepository;

    public WeatherController(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    @GetMapping("/weather")
    public Flux<WeatherData> getWeatherData(@RequestParam String cityName, @RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        return weatherDataRepository.findByCityNameAndDate(cityName, parsedDate);
    }

    @GetMapping("/all")
    public Flux<WeatherData> getAllWeatherData() {
        return weatherDataRepository.findAll();
    }
}
