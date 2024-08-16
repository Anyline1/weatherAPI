package ru.anyline.weatherapi.model;

import lombok.Data;

import java.time.LocalDate;
@Data
public class WeatherDataDTO {

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
