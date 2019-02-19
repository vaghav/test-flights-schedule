package com.ryanair.flights.services;

import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * Service for getting all possible flight routes via calling API
 * https://services-api.ryanair.com/locate/3/routes/
 */
@Service
@Slf4j
public class RouteService {

    private static final String URL = "https://services-api.ryanair.com/locate/3/routes";

    private final RestTemplate restTemplate;

    @Autowired
    public RouteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RouteDTO> getFlightRoutes() {
        try {
            RouteDTO[] response = restTemplate.getForObject(URI.create(URL), RouteDTO[].class);
            return List.of(response);
        } catch (RuntimeException ex) {
            log.error("Could't get flight routes ");
            return Collections.emptyList();
        }
    }
}
