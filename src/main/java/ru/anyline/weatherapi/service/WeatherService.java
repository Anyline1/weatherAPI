package ru.anyline.weatherapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.anyline.weatherapi.entity.WeatherDTO;
import ru.anyline.weatherapi.entity.WeatherData;
import ru.anyline.weatherapi.WeatherRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WeatherService implements WService {

    private final RestTemplate restTemplate;
    private final WeatherRepository weatherRepository;
    private final RedisTemplate<String, WeatherData> redisTemplate;
    private final KafkaProducerService kafkaProducerService;


    @Value("${weather.api.openWeatherMapUrl}")
    private String openWeatherMapUrl;

    @Value("${weather.api.weatherApiUrl}")
    private String weatherApiUrl;

    @Value("${weather.api.provider}")
    private String provider;

    @Value("${weather.api.key}")
    private String apiKey;



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

    private double kelvinToCelsius(double temperature) {
        return Math.round((temperature - 273.15) * 10.0) / 10.0;
    }

    private WeatherData convertToWeatherData(WeatherDTO response) {

        return WeatherData.builder()
                .city(response.getName())
                .temperature(kelvinToCelsius(response.getMain().getTemp()))
                .humidity(response.getMain().getHumidity())
                .pressure(response.getMain().getPressure())
                .windSpeed(response.getWind().getSpeed())
                .cloudiness(response.getClouds().getAll())
                .minTemp(kelvinToCelsius(response.getMain().getTempMin()))
                .maxTemp(kelvinToCelsius(response.getMain().getTempMax()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Scheduled(fixedRateString = "${timer.cache-ttl}")
    public void clearCache() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands();
    }

    public WeatherData getWeather(String city) {
        String apiUrl = buildApiUrl(city);
        WeatherDTO response = restTemplate.getForObject(apiUrl, WeatherDTO.class);
        WeatherData data = null;
        if (response != null) {
            data = convertToWeatherData(response);
        }
        return data;
    }

}

