package ru.anyline.weatherapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cityName;
    private LocalDate date;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private double cloudiness;
    private double minTemperature;
    private double maxTemperature;
    private double pressure;

}
