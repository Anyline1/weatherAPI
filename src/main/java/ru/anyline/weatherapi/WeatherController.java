package ru.anyline.weatherapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherRepository weatherRepository;
    private final RedisTemplate<String, WeatherData> redisTemplate;

    @Autowired
    public WeatherController(WeatherRepository weatherRepository,
                             RedisTemplate<String, WeatherData> redisTemplate) {
        this.weatherRepository = weatherRepository;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/current")
    public ResponseEntity<WeatherData> getCurrentWeather(@RequestParam String city) {
        WeatherData cachedData = redisTemplate.opsForValue().get(city);
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

