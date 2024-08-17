CREATE TABLE weather_data (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              city_name VARCHAR(255),
                              date DATE,
                              temperature DOUBLE,
                              min_temperature DOUBLE,
                              max_temperature DOUBLE,
                              humidity DOUBLE,
                              wind_speed DOUBLE,
                              cloudiness DOUBLE,
                              pressure DOUBLE
);
