package com.ryanair.flights.util;

import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import com.ryanair.flights.internal.dto.FlightConnectionDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class RouteUtil {

    /**
     *  Filter and collect all connections which are having one transit stop
     * @param departAirport
     * @param arrivalAirport
     * @param routes
     * @return
     */
    public static List<FlightConnectionDTO> getOneStopConnections(String departAirport,
                                                                  String arrivalAirport,
                                                                  List<RouteDTO> routes) {
        Set<String> firstFlightArrivalAirportSet = routes
                .stream()
                .filter(route -> route.getAirportFrom().equals(departAirport))
                .map(route -> route.getAirportTo())
                .collect(toSet());

        return routes
                .stream()
                .filter(route -> route.getAirportTo().equals(arrivalAirport))
                .filter(route -> firstFlightArrivalAirportSet.contains(route.getAirportFrom()))
                .map(route -> new FlightConnectionDTO(new RouteDTO(departAirport, route.getAirportFrom()),
                        new RouteDTO(route.getAirportFrom(), arrivalAirport)))
                .collect(toList());
    }

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
