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
import java.util.Objects;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final WeatherRepository weatherRepository;
    private final RedisTemplate<String, WeatherData> redisTemplate;

    @Value("${weather.api.openWeatherMapUrl}")
    private String openWeatherMapUrl;

    @Value("${weather.api.weatherApiUrl}")
    private String weatherApiUrl;

    @Value("${weather.api.provider}")
    private String provider;

    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    public WeatherService(RestTemplateBuilder builder,
                          WeatherRepository weatherRepository,
                          RedisTemplate<String, WeatherData> redisTemplate) {
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
            return openWeatherMapUrl + city + "&appid=" + apiKey;
        } else {
            return weatherApiUrl + apiKey + "&q=" + city;
        }
    }

    private WeatherData convertToWeatherData(WeatherDTO response) {

        return WeatherData.builder()
                .city(response.getName())
                .temperature(response.getMain().getTemp())
                .humidity(response.getMain().getHumidity())
                .pressure(response.getMain().getPressure())
                .windSpeed(response.getWind().getSpeed())
                .cloudiness(response.getClouds().getAll())
                .minTemp(response.getMain().getTempMin())
                .maxTemp(response.getMain().getTempMax())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Scheduled(fixedRateString = "${timer.cache-ttl}")
    public void clearCache() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands();
    }

}

