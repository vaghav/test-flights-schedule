package com.ryanair.flights.services;

import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import com.ryanair.flights.internal.dto.FlightConnectionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Finding all possible routes from arrival to destination airport using
 * recursive breadth-first search algorithm
 */
@Service
public class RouteFinder {

    public List<FlightConnectionDTO> findRoutes(String arrivalAirport,
                                                String destinationAirport,
                                                List<RouteDTO> flightRoutes) {

        System.out.println("Arrival airport: " + arrivalAirport + " Destin airport: " + destinationAirport);

        List<RouteDTO> foundRoutes = new ArrayList<>();
        List<FlightConnectionDTO> flightConnections = new ArrayList<>();

        Set<String> visitedAirport = new HashSet<>();

        findConnections(arrivalAirport, destinationAirport, foundRoutes, flightRoutes, visitedAirport);
        IntStream.range(0, foundRoutes.size() - 1)
                .forEach(i -> flightConnections.add(new FlightConnectionDTO(foundRoutes.get(i), foundRoutes.get(i + 1))));
        return flightConnections;
    }

    private void findConnections(String arrivalAirport, String destinationAirport,
                                 List<RouteDTO> foundRoutes,
                                 List<RouteDTO> routes,
                                 Set<String> visitedAirports) {

        visitedAirports.add(arrivalAirport);

        if (arrivalAirport.equals(destinationAirport)) {
            printFlights(foundRoutes);
        }

        for (String neighbourAirport : getNeighbourAirports(arrivalAirport, routes)) {
            if (!visitedAirports.contains(neighbourAirport)) {
                foundRoutes.add(new RouteDTO(arrivalAirport, neighbourAirport));
                findConnections(neighbourAirport, destinationAirport, foundRoutes, routes, visitedAirports);
                foundRoutes = removeRoute(foundRoutes, arrivalAirport);
            }
        }
        visitedAirports.add(arrivalAirport);
    }

    private Set<String> getNeighbourAirports(String airport, List<RouteDTO> routes) {
        return routes.stream()
                .filter(route -> route.getAirportFrom().equals(airport))
                .map(route -> route.getAirportTo())
                .collect(toSet());
    }

    private List<RouteDTO> removeRoute(List<RouteDTO> flightConnections, String arrivalAirport) {
        return flightConnections
                .stream()
                .filter(flightRouteDTO -> flightRouteDTO.getAirportTo().equals(arrivalAirport))
                .collect(toList());
    }

    private void printFlights(List<RouteDTO> routes) {
        routes.forEach(route ->
                System.out.println("=======" + route.getAirportFrom() + "======" + route.getAirportTo()));
    }
}
