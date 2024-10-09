package ru.anyline.weatherapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anyline.weatherapi.entity.AirPollutionData;

@Repository
public interface AirPollutionRepository extends JpaRepository<AirPollutionData, Long> {
}
