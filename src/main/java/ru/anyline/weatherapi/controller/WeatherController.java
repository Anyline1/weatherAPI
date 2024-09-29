package ru.anyline.weatherapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anyline.weatherapi.WeatherData;
import ru.anyline.weatherapi.WeatherRepository;
import ru.anyline.weatherapi.service.WeatherService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherRepository weatherRepository;
    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<WeatherData> getCurrentWeather(@RequestParam String city) throws JsonProcessingException {

    if (!city.matches("[a-zA-Z ]+") || city.isEmpty() || city.length() > 49) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    WeatherData cachedData = weatherService.getWeather(city);

    if (cachedData != null) {
        return ResponseEntity.ok(cachedData);
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
    List<WeatherData> data = weatherRepository.findByCityAndTimestampBetween(city, startOfDay, now);

    if (!data.isEmpty()) {
        return ResponseEntity.ok(data.get(data.size() - 1));
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
}


    @GetMapping("/all")
    public List<WeatherData> getAllWeatherData() {
        return weatherRepository.findAll();
    }
}

