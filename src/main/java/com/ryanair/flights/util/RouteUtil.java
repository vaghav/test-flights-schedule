package com.ryanair.flights.util;

import com.ryanair.flights.downstream.dto.RouteDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class RouteUtil {

    public static boolean isDirectRouteExists(String departAirport,
                                              String arrivalAirport,
                                              List<RouteDTO> routes) {
        return routes
                .stream()
                .anyMatch(route -> isRouteContainsAirports(route, departAirport, arrivalAirport));
    }

    private static boolean isRouteContainsAirports(RouteDTO route,
                                                  String departAirport,
                                                  String arrivalAirport) {
        return route.getAirportFrom().equals(departAirport) && route.getAirportTo().equals(arrivalAirport);
    }
}
