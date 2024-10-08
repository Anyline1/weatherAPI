package ru.anyline.weatherapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anyline.weatherapi.entity.WeatherData;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {

    List<WeatherData> findByCityAndTimestampBetween(String city, LocalDateTime start, LocalDateTime end);
    List<WeatherData> findTop4ByOrderByTimestampDesc();

}
