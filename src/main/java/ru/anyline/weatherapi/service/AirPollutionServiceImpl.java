package ru.anyline.weatherapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.anyline.weatherapi.entity.AirPollutionDTO;
import ru.anyline.weatherapi.entity.AirPollutionData;
import ru.anyline.weatherapi.repository.AirPollutionRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AirPollutionServiceImpl implements AirPollutionService{

    private final AirPollutionRepository airPollutionRepository;

    @Value("${weather.api.openWeatherMapAirUrl}")
    private String openWeatherMapAirUrl;

    @Value("${weather.api.key}")
    private String apiKey;
    @Override
    public void fetchAirPollutionData() {

        RestTemplate restTemplate = new RestTemplate();
        String url = openWeatherMapAirUrl.replace("{API_KEY}", apiKey);

        var response = restTemplate.getForObject(url, AirPollutionDTO.class);

        if (response != null && response.getList() != null) {
            for (var data : response.getList()) {
                AirPollutionData record = new AirPollutionData();
                record.setLatitude(response.getCoord().get(0));
                record.setLongitude(response.getCoord().get(1));

                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(data.getDt()), ZoneOffset.UTC);
                record.setDateTime(dateTime);

                record.setAqi(data.getMain().getAqi());

                var components = data.getComponents();
                record.setCo(components.getCo());
                record.setNo(components.getNo());
                record.setNo2(components.getNo2());
                record.setO3(components.getO3());
                record.setSo2(components.getSo2());
                record.setPm2_5(components.getPm2_5());
                record.setPm10(components.getPm10());
                record.setNh3(components.getNh3());

                airPollutionRepository.save(record);
            }
        }
    }
}
