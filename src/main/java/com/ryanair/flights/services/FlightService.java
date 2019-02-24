package com.ryanair.flights.services;

import com.ryanair.flights.dto.ItineraryDTO;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

public abstract class FlightService implements FlightsSearchService {

    @Value("${flights.interval:2}")
    private Integer flightsIntervalInHours;

    @Value("${flights.stops.count:2}")
    private Integer flightsStopsCount;

    public List<ItineraryDTO> searchInterConnected(String departureAirport, String arrivalAirport,
                                                   LocalDateTime departureTime, LocalDateTime arrivalTime) {

        return searchInterConnected(departureAirport, arrivalAirport, departureTime, arrivalTime,
                flightsIntervalInHours);

    }

    public List<ItineraryDTO> searchInterConnected(String departureAirport, String arrivalAirport,
                                                   LocalDateTime departureTime, LocalDateTime arrivalTime,
                                                   int flightsIntervalInHours) {
        return searchInterConnected(departureAirport, arrivalAirport, departureTime, arrivalTime,
                flightsIntervalInHours, flightsStopsCount);
    }

    public abstract List<ItineraryDTO> searchInterConnected(String departureAirport, String arrivalAirport,
                                                            LocalDateTime departureTime, LocalDateTime arrivalTime,
                                                            int flightsIntervalInHours, int stopsCount);
}
