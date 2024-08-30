package ru.anyline.weatherapi.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.anyline.weatherapi.WeatherData;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "weather-topic";

    private final KafkaTemplate<String, WeatherData> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, WeatherData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendWeatherData(String city, WeatherData weatherData) {
        kafkaTemplate.send(TOPIC, city, weatherData);
        System.out.println("Sent weather data for city: " + city);
    }
}

