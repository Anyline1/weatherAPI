package ru.anyline.weatherapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anyline.weatherapi.entity.AirPollutionDTO;
import ru.anyline.weatherapi.entity.AirPollutionData;
import ru.anyline.weatherapi.service.AirPollutionService;

@RestController
@AllArgsConstructor
public class AirPollutionController {

    private AirPollutionService airPollutionService;

    @GetMapping("/airpollution")
    public ResponseEntity<AirPollutionDTO> fetchAirPollutionData() {
        AirPollutionDTO data = airPollutionService.fetchAirPollutionData();
        return ResponseEntity.ok(data);
    }
}
