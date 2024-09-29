package ru.anyline.weatherapi.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.anyline.weatherapi.entity.WeatherData;

@Service
@AllArgsConstructor
public class KafkaProducerService implements KafkaService{

    private static final String TOPIC = "weather-topic";
    private final KafkaTemplate<String, WeatherData> kafkaTemplate;

    public void sendWeatherData(String city, WeatherData weatherData) {
        kafkaTemplate.send(TOPIC, city, weatherData);
    }
}

