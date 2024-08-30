package ru.anyline.weatherapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.anyline.weatherapi.WeatherDTO;
import ru.anyline.weatherapi.WeatherData;
import ru.anyline.weatherapi.WeatherRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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

    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public WeatherService(RestTemplateBuilder builder,
                          ObjectMapper objectMapper,
                          WeatherRepository weatherRepository,
                          RedisTemplate<String, WeatherData> redisTemplate, KafkaProducerService kafkaProducerService) {
        this.restTemplate = builder.build();
        this.objectMapper = objectMapper;
        this.weatherRepository = weatherRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaProducerService = kafkaProducerService;
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
                kafkaProducerService.sendWeatherData(city, data);
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

    public WeatherData getWeather(String city) throws JsonProcessingException {

        Object cachedData = redisTemplate.opsForValue().get(city);

        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) cachedData;

        String jsonString = objectMapper.writeValueAsString(map);

        return objectMapper.readValue(jsonString, WeatherData.class);

    }

}

