package ru.anyline.weatherapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.anyline.weatherapi.model.WeatherData;
import ru.anyline.weatherapi.model.WeatherDataDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findByCityNameAndDate(String cityName, LocalDate date);
    List<WeatherData> findByDateBefore(LocalDate expirationDate);
    Optional<WeatherData> findTopByCityNameAndDate(String cityName, LocalDate data);
}
