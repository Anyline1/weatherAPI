package ru.anyline.weatherapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDTO {

    private String name;
    private Main main;
    private Wind wind;
    private Clouds clouds;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {

        @JsonProperty("temp")
        private double temp;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
        private int pressure;
        private int humidity;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {

        private double speed;
        private int deg;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clouds {

        private int all;

    }
}
