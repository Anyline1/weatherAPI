package ru.anyline.weatherapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.anyline.weatherapi.controller.WeatherController;
import ru.anyline.weatherapi.model.WeatherDataDTO;
import ru.anyline.weatherapi.service.WeatherService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class WeatherControllerTest {
    private LocalDate pastDate;
    @Mock
    private WeatherService weatherService;

    @Captor
    private ArgumentCaptor<String> cityNameCaptor;

    @Captor
    private ArgumentCaptor<LocalDate> localDateCaptor;

    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pastDate = LocalDate.now().minusDays(1);
        weatherController = new WeatherController(weatherService, null, null, null);
    }

    @Test
    void getForecastWeatherData_validCityNameAndDate_returnsValidWeatherDataDTO() {
        // Given
        String cityName = "Moscow";
        LocalDate date = LocalDate.of(2024, 8, 22);
        WeatherDataDTO expectedWeatherDataDTO = WeatherDataDTO.builder()
                .cityName("Moscow")
                .date(pastDate)
                .temperature(25.1)
                .minTemperature(56)
                .maxTemperature(4.86)
                .humidity(54)
                .pressure(24.2)
                .windSpeed(25.8)
                .cloudiness(1013)
                .build();

        when(weatherService.getWeatherData(cityNameCaptor.capture(), localDateCaptor.capture()))
                .thenReturn(Optional.of(expectedWeatherDataDTO));

        Optional<WeatherDataDTO> actualWeatherDataDTO = weatherController.getForecastWeatherData(cityName, date.toString());

        assertTrue(actualWeatherDataDTO.isPresent());
        assertEquals(expectedWeatherDataDTO, actualWeatherDataDTO.get());
        verify(weatherService).getWeatherData(cityNameCaptor.capture(), localDateCaptor.capture());
        assertEquals(cityName, cityNameCaptor.getValue());
        assertEquals(date, localDateCaptor.getValue());
    }

    @Test
    public void getForecastWeatherData_whenDateIsPast_shouldReturnEmptyOptional() {
        String cityName = "New York";
        when(weatherService.getWeatherData(cityName, pastDate)).thenReturn(Optional.empty());

        Optional<WeatherDataDTO> weatherDataDTO = weatherController.getForecastWeatherData(cityName, pastDate.toString());

        assertTrue(weatherDataDTO.isEmpty());
    }

    @Test
    void getForecastWeatherData_whenCityNameIsNotFound_shouldReturnEmptyOptional() {
        String cityName = "NonExistentCity";
        LocalDate date = LocalDate.now().plusDays(1);
        when(weatherService.getWeatherData(cityName, date)).thenReturn(Optional.empty());

        Optional<WeatherDataDTO> weatherDataDTO = weatherController.getForecastWeatherData(cityName, date.toString());

        assertTrue(weatherDataDTO.isEmpty());
    }

    @Test
    void getForecastWeatherData_shouldCacheWeatherDataForSpecificCityAndDate() {
        String cityName = "Moscow";
        LocalDate date = LocalDate.of(2024, 8, 22);
        WeatherDataDTO expectedWeatherDataDTO = WeatherDataDTO.builder()
                .cityName("Moscow")
                .date(date)
                .temperature(20.5)
                .minTemperature(50)
                .maxTemperature(17.3)
                .humidity(86)
                .pressure(14.5)
                .windSpeed(26.3)
                .cloudiness(0)
                .build();

        when(weatherService.getWeatherData(cityName, date)).thenReturn(Optional.of(expectedWeatherDataDTO));

        Optional<WeatherDataDTO> actualWeatherDataDTO1 = weatherController.getForecastWeatherData(cityName, date.toString());
        Optional<WeatherDataDTO> actualWeatherDataDTO2 = weatherController.getForecastWeatherData(cityName, date.toString());

        assertTrue(actualWeatherDataDTO1.isPresent());
        assertEquals(expectedWeatherDataDTO, actualWeatherDataDTO1.get());
        verify(weatherService, times(2)).getWeatherData(cityName, date);
        assertTrue(actualWeatherDataDTO2.isPresent());
        assertEquals(expectedWeatherDataDTO, actualWeatherDataDTO2.get());
    }
}