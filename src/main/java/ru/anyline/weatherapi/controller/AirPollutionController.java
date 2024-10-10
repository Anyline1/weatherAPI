package ru.anyline.weatherapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.anyline.weatherapi.entity.AirPollutionDTO;
import ru.anyline.weatherapi.service.AirPollutionService;

@RestController
@AllArgsConstructor
public class AirPollutionController {

    private final AirPollutionService airPollutionService;

    @GetMapping("/airpollution")
    public ResponseEntity<AirPollutionDTO> fetchAirPollutionData(@RequestParam String city) {

        if (!city.matches("[a-zA-Z ]+") || city.isEmpty() || city.length() > 49) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        AirPollutionDTO data = airPollutionService.fetchAirPollutionData();
        return ResponseEntity.ok(data);
    }
}
