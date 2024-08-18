package ru.anyline.weatherapi.service;

import org.springframework.stereotype.Service;
import ru.anyline.weatherapi.model.WeatherData;
import ru.anyline.weatherapi.model.WeatherDataDTO;
import ru.anyline.weatherapi.repository.WeatherDataRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final WeatherDataRepository weatherDataRepository;


    public WeatherServiceImpl(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    @Override
    public Optional<WeatherDataDTO> getWeatherData(String cityName, LocalDate date) {
        return weatherDataRepository.findByCityNameAndDate(cityName, date)
                .map(this::convertToDTO);
    }



    private WeatherDataDTO convertToDTO(WeatherData weatherData) {
        return WeatherDataDTO.builder()
                .cityName(weatherData.getCityName())
                .date(weatherData.getDate())
                .temperature(weatherData.getTemperature())
                .minTemperature(weatherData.getMinTemperature())
                .maxTemperature(weatherData.getMaxTemperature())
                .humidity(weatherData.getHumidity())
                .pressure(weatherData.getPressure())
                .windSpeed(weatherData.getWindSpeed())
                .cloudiness(weatherData.getCloudiness())
                .build();
    }
}
