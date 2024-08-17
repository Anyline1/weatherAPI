package ru.anyline.weatherapi.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.anyline.weatherapi.client.WeatherApiClient;
import ru.anyline.weatherapi.client.OpenWeatherMapClient;
import ru.anyline.weatherapi.model.WeatherData;
import ru.anyline.weatherapi.model.WeatherDataDTO;
import ru.anyline.weatherapi.repository.WeatherDataRepository;

import java.time.LocalDate;

@Component
public class WeatherScheduler {

    private final WeatherApiClient weatherApiClient;
    private final OpenWeatherMapClient openWeatherMapClient;
    private final WeatherDataRepository weatherDataRepository;

    @Value("${weather.scheduler.city}")
    private String cityName;
    public WeatherScheduler(WeatherApiClient weatherApiClient, OpenWeatherMapClient openWeatherMapClient, WeatherDataRepository weatherDataRepository) {
        this.weatherApiClient = weatherApiClient;
        this.openWeatherMapClient = openWeatherMapClient;
        this.weatherDataRepository = weatherDataRepository;
    }

    @Scheduled(fixedRateString = "${weather.timer.interval}")
    public void scheduledFetchWeatherData() {

        LocalDate date = LocalDate.now();

        fetchWeatherData(cityName, date);
    }

    public void fetchWeatherData(String cityName, LocalDate date) {

        Mono<WeatherDataDTO> weatherApiMono = weatherApiClient.fetchWeatherData(cityName, date);
        Mono<WeatherDataDTO> openWeatherMapMono = openWeatherMapClient.fetchWeatherData(cityName, date);

        weatherApiMono
                .map(this::convertToWeatherData)
                .flatMap(weatherDataRepository::save)
                .doOnSuccess(savedData -> System.out.println("Weather data from WeatherApi saved: " + savedData))
                .then(openWeatherMapMono
                        .map(this::convertToWeatherData)
                        .flatMap(weatherDataRepository::save))
                .doOnSuccess(savedData -> System.out.println("Weather data from OpenWeatherMap saved: " + savedData))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        savedData -> {},
                        error -> System.err.println("Failed to save weather data: " + error.getMessage())
                );
    }

    private WeatherData convertToWeatherData(WeatherDataDTO dto) {

        WeatherData weatherData = new WeatherData();
        weatherData.setCityName(dto.getCityName());
        weatherData.setDate(dto.getDate());
        weatherData.setTemperature(dto.getTemperature());
        weatherData.setHumidity(dto.getHumidity());
        weatherData.setWindSpeed(dto.getWindSpeed());
        weatherData.setCloudiness(dto.getCloudiness());
        weatherData.setMinTemperature(dto.getMinTemperature());
        weatherData.setMaxTemperature(dto.getMaxTemperature());
        weatherData.setPressure(dto.getPressure());
        return weatherData;
    }

    @Scheduled(cron = "${weather.cache.cleanup}")
    public void cleanupCache() {
        weatherDataRepository.deleteAll()
                .doOnSuccess(res -> System.out.println("Deleted successfully"))
                .doOnError(res -> System.out.println("Error accused"))
                .subscribe();
    }
}




