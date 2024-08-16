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
        WeatherDataDTO dto = new WeatherDataDTO();
        dto.setCityName(weatherData.getCityName());
        dto.setDate(weatherData.getDate());
        dto.setTemperature(weatherData.getTemperature());
        dto.setHumidity(weatherData.getHumidity());
        dto.setWindSpeed(weatherData.getWindSpeed());
        dto.setCloudiness(weatherData.getCloudiness());
        dto.setMinTemperature(weatherData.getMinTemperature());
        dto.setMaxTemperature(weatherData.getMaxTemperature());
        dto.setPressure(weatherData.getPressure());
        return dto;
    }
}
