package com.ryanair.flights.services;

import com.ryanair.flights.downstream.dto.RouteDTO;
import com.ryanair.flights.internal.dto.FlightConnectionDTO;

import java.util.List;

/**
 * Service for getting all possible direct flight routes via calling Route API
 * https://services-api.ryanair.com/locate/3/routes/
 */
public interface RouteService {

    /**
     * Get all possible direct routes
     * @return
     */
    List<RouteDTO> getFlightRoutes();

    /**
     *  Filter and collect all connections which are having one transit stop
     * @param departAirport
     * @param arrivalAirport
     * @param routes
     * @return
     */
    List<FlightConnectionDTO> getOneStopConnections(String departAirport,
                                                    String arrivalAirport,
                                                    List<RouteDTO> routes);
    /**
     * Find possible connection from arrival to destination airport
     * @param arrivalAirport
     * @param destinationAirport
     * @param flightRoutes
     * @return
     */
    List<FlightConnectionDTO> findRoutes(String arrivalAirport,
                                         String destinationAirport,
                                         List<RouteDTO> flightRoutes);
}
