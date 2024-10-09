package ru.anyline.weatherapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirPollutionRepository extends JpaRepository<AirPollutionData, Long> {
}
