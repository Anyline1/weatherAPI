package ru.anyline.weatherapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.anyline.weatherapi.entity.WeatherData;
import ru.anyline.weatherapi.repository.WeatherRepository;

import java.util.List;

@Controller
@AllArgsConstructor
public class WeatherViewController {

    private final WeatherRepository weatherRepository;

    @GetMapping("/weather")
    public String getWeatherData(Model model) {
        List<WeatherData> weatherList = weatherRepository.findTop4ByOrderByTimestampDesc();
        model.addAttribute("weatherList", weatherList);
        return "weather";
    }
}

