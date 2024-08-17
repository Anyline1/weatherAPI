package ru.anyline.weatherapi.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Table("weather_data")
@Data
@NoArgsConstructor
public class WeatherData {

    @Id
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


    public WeatherData(String cityName,
                       LocalDate date,
                       double temperature,
                       double minTemperature,
                       double maxTemperature,
                       double humidity,
                       double windSpeed,
                       double cloudiness,
                       double pressure) {
    }
}
