package ru.anyline.weatherapi.service;

import ru.anyline.weatherapi.entity.AirPollutionDTO;

public interface AirPollutionService {

    AirPollutionDTO fetchAirPollutionData();
}
