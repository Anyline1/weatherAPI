package ru.anyline.weatherapi.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.anyline.weatherapi.client.OpenWeatherMapClient;
import ru.anyline.weatherapi.client.WeatherApiClient;
import ru.anyline.weatherapi.model.WeatherData;
import ru.anyline.weatherapi.model.WeatherDataDTO;
import ru.anyline.weatherapi.repository.WeatherDataRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final WeatherDataRepository weatherDataRepository;
    private final OpenWeatherMapClient openWeatherMapClient;

    public WeatherServiceImpl(WeatherDataRepository weatherDataRepository, OpenWeatherMapClient openWeatherMapClient) {
        this.weatherDataRepository = weatherDataRepository;
        this.openWeatherMapClient = openWeatherMapClient;
    }

    @Override
    @Cacheable(value = "weatherCache", key = "#cityName + #date")
    public Optional<WeatherDataDTO> getWeatherData(String cityName, LocalDate date) {
        return Optional.of(weatherDataRepository.findTopByCityNameAndDate(cityName, date)
                .map(this::convertToDTO)
                .orElseGet(() -> {
                    WeatherDataDTO weatherDataDTO = openWeatherMapClient.fetchWeatherData(cityName, date);

                    WeatherData weatherData = convertToEntity(weatherDataDTO);
                    weatherDataRepository.save(weatherData);

                    return weatherDataDTO;
                }));
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

    private WeatherData convertToEntity(WeatherDataDTO weatherDataDTO){
        return WeatherData.builder()
                .cityName(weatherDataDTO.getCityName())
                .date(weatherDataDTO.getDate())
                .temperature(weatherDataDTO.getTemperature())
                .minTemperature(weatherDataDTO.getMinTemperature())
                .maxTemperature(weatherDataDTO.getMaxTemperature())
                .humidity(weatherDataDTO.getHumidity())
                .windSpeed(weatherDataDTO.getWindSpeed())
                .cloudiness(weatherDataDTO.getCloudiness())
                .pressure(weatherDataDTO.getPressure())
                .build();

    }
}
