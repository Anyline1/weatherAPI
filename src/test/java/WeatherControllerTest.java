import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.anyline.weatherapi.WeatherController;
import ru.anyline.weatherapi.WeatherData;
import ru.anyline.weatherapi.WeatherRepository;
import ru.anyline.weatherapi.service.WeatherService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
}