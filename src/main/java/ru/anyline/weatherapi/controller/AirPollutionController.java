package ru.anyline.weatherapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.anyline.weatherapi.service.AirPollutionService;

@RestController
@AllArgsConstructor
public class AirPollutionController {

    private AirPollutionService airPollutionService;

    @GetMapping("/fetchair")
    public String fetchAirPollutionData() {
        airPollutionService.fetchAirPollutionData();
        return "to do";
    }
}
