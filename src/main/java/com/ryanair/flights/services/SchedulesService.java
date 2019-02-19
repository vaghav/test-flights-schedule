package com.ryanair.flights.services;

import com.ryanair.flights.internal.downstream.dto.ScheduleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

/**
 * Service for getting flight schedulers by year ans month via calling API
 * https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}
 * Example:
 * https://services-api.ryanair.com/timtbl/3/schedules/DUB/WRO/years/2016/months/6:
 */
@Service
@Slf4j
public class SchedulesService {


    private final RestTemplate restTemplate;

    @Autowired
    public SchedulesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ScheduleDTO> getFlightsSchedule(String departureAirport,
                                                    String arrivalAirport,
                                                    int year, int month) {

        String URL = String.format("https://services-api.ryanair.com/timtbl/3/schedules/%s/%s/years/%d/months/%d",
                departureAirport, arrivalAirport, year, month);
        try {
            return Optional.ofNullable(restTemplate.getForObject(URI.create(URL), ScheduleDTO.class));
        } catch (RuntimeException ex) {
           log.error("Could't get flight schedule ");
           return Optional.empty();
        }
    }
}
