package ru.anyline.weatherapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final WeatherRepository weatherRepository;
    private final RedisTemplate<String, WeatherData> redisTemplate;

    @Value("${weather.api.provider}")
    private String provider;

    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    public WeatherService(RestTemplateBuilder builder, WeatherRepository weatherRepository, RedisTemplate<String, WeatherData> redisTemplate) {
        this.restTemplate = builder.build();
        this.weatherRepository = weatherRepository;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRateString = "${timer.interval}")
    public void fetchAndStoreWeatherData() {
        List<String> cities = Arrays.asList("London", "New York", "Paris", "Tokyo");

        for (String city : cities) {
            String apiUrl = buildApiUrl(city);

            WeatherDTO response = restTemplate.getForObject(apiUrl, WeatherDTO.class);
            if (response != null) {
                WeatherData data = convertToWeatherData(response);
                weatherRepository.save(data);
                redisTemplate.opsForValue().set(city, data, Duration.ofHours(1));
            }
        }
    }

    private String buildApiUrl(String city) {
        if ("openweathermap".equals(provider)) {
            return "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
        } else {
            return "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + city;
        }
    }

    private WeatherData convertToWeatherData(WeatherDTO response) {

        WeatherData data = new WeatherData();
        data.setCity(response.getName());
        data.setTemperature(response.getMain().getTemp());
        data.setHumidity(response.getMain().getHumidity());
        data.setPressure(response.getMain().getPressure());
        data.setWindSpeed(response.getWind().getSpeed());
        data.setCloudiness(response.getClouds().getAll());
        data.setMinTemp(response.getMain().getTempMin());
        data.setMaxTemp(response.getMain().getTempMax());
        data.setTimestamp(LocalDateTime.now());

        return data;
    }
}

