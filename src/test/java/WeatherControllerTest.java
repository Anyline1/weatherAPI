import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.anyline.weatherapi.WeatherController;
import ru.anyline.weatherapi.WeatherData;
import ru.anyline.weatherapi.WeatherRepository;
import ru.anyline.weatherapi.service.WeatherService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class WeatherControllerTest {

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    private WeatherData weatherData;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        MockitoAnnotations.initMocks(this);

        weatherData = WeatherData.builder()
                .city("City")
                .timestamp(LocalDateTime.now())
                .temperature(25.0)
                .build();
        when(weatherService.getWeather(any(String.class))).thenReturn(null);
        when(weatherRepository.findByCityAndTimestampBetween(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(weatherData));
    }

    @Test
    public void shouldReturnCurrentWeatherDataForValidCity() throws JsonProcessingException {
        ResponseEntity<WeatherData> responseEntity = weatherController.getCurrentWeather("City");

        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.ok(weatherData), responseEntity);
    }

    @Test
    public void shouldReturnCachedWeatherDataWhenAvailable() throws JsonProcessingException {
        WeatherData cachedData = WeatherData.builder()
                .city("City")
                .timestamp(LocalDateTime.now())
                .temperature(25.0)
                .build();
        when(weatherService.getWeather(any(String.class))).thenReturn(cachedData);

        ResponseEntity<WeatherData> responseEntity = weatherController.getCurrentWeather("City");

        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.ok(cachedData), responseEntity);
    }

    @Test
    public void shouldReturnLatestWeatherDataForCityWhenCachedDataNotAvailable() throws JsonProcessingException {
        when(weatherService.getWeather(any(String.class))).thenReturn(null);
        when(weatherRepository.findByCityAndTimestampBetween(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(weatherData));

        ResponseEntity<WeatherData> responseEntity = weatherController.getCurrentWeather("City");

        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.ok(weatherData), responseEntity);
    }

    @Test
    public void shouldReturnNotFoundStatusWhenWeatherDataNotFoundForCity() throws JsonProcessingException {
        when(weatherService.getWeather(any(String.class))).thenReturn(null);
        when(weatherRepository.findByCityAndTimestampBetween(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ResponseEntity<WeatherData> responseEntity = weatherController.getCurrentWeather("City");

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void shouldHandleConcurrentRequestsForSameCity() throws InterruptedException, ExecutionException {
        String city = "City";
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<ResponseEntity<WeatherData>> task1 = () -> weatherController.getCurrentWeather(city);
        Callable<ResponseEntity<WeatherData>> task2 = () -> weatherController.getCurrentWeather(city);

        Future<ResponseEntity<WeatherData>> future1 = executorService.submit(task1);
        Future<ResponseEntity<WeatherData>> future2 = executorService.submit(task2);

        ResponseEntity<WeatherData> response1 = future1.get();
        ResponseEntity<WeatherData> response2 = future2.get();

        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(response1, response2);

        executorService.shutdown();
    }

    @Test
public void shouldValidateInputCityNameToPreventSQLInjection() throws JsonProcessingException {
    String[] invalidCityNames = {
            "City; DROP TABLE WeatherData;",
            "City' OR 1=1;",
            "City' --",
            "City'); DROP TABLE WeatherData;--"
    };

    for (String invalidCityName : invalidCityNames) {
        ResponseEntity<WeatherData> responseEntity = weatherController.getCurrentWeather(invalidCityName);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}

    @Test
    public void shouldHandleLargeAmountsOfWeatherDataForACityEfficiently() throws JsonProcessingException {
        String city = "LargeCity";
        int expectedDataSize = 10000; 

        List<WeatherData> weatherDataList = new ArrayList<>();
        for (int i = 0; i < expectedDataSize; i++) {
            weatherDataList.add(WeatherData.builder()
                    .city(city)
                    .timestamp(LocalDateTime.now())
                    .temperature(25.0 + i)
                    .build());
        }

        when(weatherService.getWeather(any(String.class))).thenReturn(null);
        when(weatherRepository.findByCityAndTimestampBetween(eq(city), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(weatherDataList);

        ResponseEntity<WeatherData> responseEntity = weatherController.getCurrentWeather(city);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(weatherDataList.get(weatherDataList.size() - 1), responseEntity.getBody());
    }

    @Test
    public void shouldReturnLatestWeatherDataWhenMultipleRequestsForSameCityWithinShortTimeFrame() throws InterruptedException, ExecutionException {
        String city = "City";
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<ResponseEntity<WeatherData>> task1 = () -> weatherController.getCurrentWeather(city);
        Callable<ResponseEntity<WeatherData>> task2 = () -> weatherController.getCurrentWeather(city);

        Future<ResponseEntity<WeatherData>> future1 = executorService.submit(task1);
        Future<ResponseEntity<WeatherData>> future2 = executorService.submit(task2);

        ResponseEntity<WeatherData> response1 = future1.get();
        ResponseEntity<WeatherData> response2 = future2.get();

        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(response1.getBody(), response2.getBody());

        executorService.shutdown();
    }

    @Test
    public void shouldReturnCachedWeatherDataWhenAvailableForCityWithinShortTimeFrame() throws JsonProcessingException {
        String city = "City";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

        WeatherData cachedData = WeatherData.builder()
                .city(city)
                .timestamp(now)
                .temperature(25.0)
                .build();

        when(weatherService.getWeather(eq(city))).thenReturn(cachedData);
        when(weatherRepository.findByCityAndTimestampBetween(eq(city), eq(startOfDay), eq(now)))
                .thenReturn(Collections.emptyList());

        ResponseEntity<WeatherData> responseEntity = weatherController.getCurrentWeather(city);

        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.ok(cachedData), responseEntity);
    }

}