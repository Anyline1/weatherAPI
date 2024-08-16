package ru.anyline.weatherapi.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.anyline.weatherapi.client.OpenWeatherMapClient;
import ru.anyline.weatherapi.client.WeatherApiClient;
import ru.anyline.weatherapi.model.WeatherData;
import ru.anyline.weatherapi.model.WeatherDataDTO;
import ru.anyline.weatherapi.repository.WeatherDataRepository;

import java.time.LocalDate;
import java.util.List;

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
    public void fetchWeatherData() {
        LocalDate date = LocalDate.now();

        WeatherDataDTO weatherDataFromApi = weatherApiClient.fetchWeatherData(cityName, date);

        WeatherDataDTO weatherDataFromOpenWeather = openWeatherMapClient.fetchWeatherData(cityName, date);

        if (weatherDataFromApi != null) {
            saveWeatherData(weatherDataFromApi);
        }
        if (weatherDataFromOpenWeather != null) {
            saveWeatherData(weatherDataFromOpenWeather);
        }
    }

    private void saveWeatherData(WeatherDataDTO weatherDataDTO) {
        WeatherData weatherData = new WeatherData();
        weatherData.setCityName(weatherDataDTO.getCityName());
        weatherData.setDate(weatherDataDTO.getDate());
        weatherData.setTemperature(weatherDataDTO.getTemperature());
        weatherData.setMinTemperature(weatherDataDTO.getMinTemperature());
        weatherData.setMaxTemperature(weatherDataDTO.getMaxTemperature());
        weatherData.setHumidity(weatherDataDTO.getHumidity());
        weatherData.setWindSpeed(weatherDataDTO.getWindSpeed());
        weatherData.setCloudiness(weatherDataDTO.getCloudiness());
        weatherData.setPressure(weatherDataDTO.getPressure());

        weatherDataRepository.save(weatherData);
    }

    @Scheduled(cron = "${weather.cache.cleanup}")
    public void getFromCache() {
        LocalDate expirationDate = LocalDate.now().minusDays(7); // Удаляем данные старше 7 дней
        List<WeatherData> oldData = weatherDataRepository.findByDateBefore(expirationDate);

        if (!oldData.isEmpty()) {
            weatherDataRepository.deleteAll(oldData);
        }
    }
}
