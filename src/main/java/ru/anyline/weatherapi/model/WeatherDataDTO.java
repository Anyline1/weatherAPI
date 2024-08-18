package ru.anyline.weatherapi.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.chrono.Chronology;

@Getter
@Setter
@Builder
public class WeatherDataDTO {
    private String cityName;
    private LocalDate date;
    private double temperature;
    private double minTemperature;
    private double maxTemperature;
    private double humidity;
    private double pressure;
    private double windSpeed;
    private double cloudiness;

}
