package com.ryanair.flights.services;

import com.ryanair.flights.enums.Operator;
import com.ryanair.flights.downstream.dto.RouteDTO;
import com.ryanair.flights.internal.dto.FlightConnectionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

    private static final String URL = "https://services-api.ryanair.com/locate/3/routes";

    private final RestTemplate restTemplate;

    @Autowired
    public RouteServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<RouteDTO> getFlightRoutes() {
        try {

            RouteDTO[] routes = restTemplate.getForObject(URI.create(URL), RouteDTO[].class);
            return Arrays.stream(routes).filter(route -> route.getConnectingAirport() == null
                    && route.getOperator().equals(Operator.RYANAIR.name())).collect(toList());

        } catch (RuntimeException ex) {
            log.error("Could't get flight routes ");
            return Collections.emptyList();
        }
    }


    @Override
    public List<FlightConnectionDTO> getOneStopConnections(String departAirport,
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

    @Override
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
