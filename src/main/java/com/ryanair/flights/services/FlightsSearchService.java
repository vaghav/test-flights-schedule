package com.ryanair.flights.services;

import com.ryanair.flights.services.dto.ItineraryDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightsSearchService {

    /**
     * Returns list of interconnected direct flights or flights with on stop.
     * @param departureAirport
     * @param arrivalAirport
     * @param departureTime
     * @param arrivalTime
     * @return
     */
    List<ItineraryDTO> searchInterConnected(String departureAirport, String arrivalAirport,
                                            LocalDateTime departureTime, LocalDateTime arrivalTime);

    /**
     * Returns list of interconnected direct flights or flights with on stop.
     * Flights difference between the arrival and the next departure
     * should be greater then {@param flightsIntervalInHours}.
     * @param departureAirport
     * @param arrivalAirport
     * @param departureTime
     * @param arrivalTime
     * @return
     */
    List<ItineraryDTO> searchInterConnected(String departureAirport, String arrivalAirport,
                                            LocalDateTime departureTime, LocalDateTime arrivalTime,
                                            int flightsIntervalInHours);

    /**
     * Returns list of interconnected direct flights or flights with fixed stop count.
     * Flights difference between the arrival and the next departure
     * should be greater then {@param flightsIntervalInHours}.
     * @param departureAirport
     * @param arrivalAirport
     * @param departureTime
     * @param arrivalTime
     * @param stopsCount
     * @return
     */
    List<ItineraryDTO> searchInterConnected(String departureAirport, String arrivalAirport,
                                            LocalDateTime departureTime, LocalDateTime arrivalTime,
                                            int flightsIntervalInHours, int stopsCount);
}
