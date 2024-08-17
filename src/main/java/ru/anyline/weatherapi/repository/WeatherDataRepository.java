package ru.anyline.weatherapi.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.anyline.weatherapi.model.WeatherData;

import java.time.LocalDate;

public interface WeatherDataRepository extends ReactiveCrudRepository<WeatherData, Long> {
    Flux<WeatherData> findByCityNameAndDate(String cityName, LocalDate date);
    Mono<Void> deleteAll();
}
