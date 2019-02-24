package com.ryanair.flights.services;

import com.ryanair.flights.downstream.dto.RouteDTO;
import com.ryanair.flights.dto.ItineraryDTO;
import com.ryanair.flights.internal.dto.FlightConnectionDTO;
import com.ryanair.flights.internal.dto.FlightInfoDTO;
import com.ryanair.flights.util.FlightSearchServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ryanair.flights.util.DateUtil.checkPeriodBetweenFlights;
import static com.ryanair.flights.util.FlightSearchServiceUtil.createItinerary;
import static com.ryanair.flights.util.RouteUtil.isDirectRouteExists;
import static java.util.stream.Collectors.toList;

@Service
public class FlightsSearchServiceImpl extends FlightService {

    private final RouteService routeService;

    private final SchedulesService schedulesService;

    @Autowired
    public FlightsSearchServiceImpl(RouteServiceImpl routeService, SchedulesServiceImpl schedulesService) {
        this.routeService = routeService;
        this.schedulesService = schedulesService;
    }

    @Override
    public List<ItineraryDTO> searchInterConnected(String departureAirport,
                                                   String arrivalAirport,
                                                   LocalDateTime departureDateTime,
                                                   LocalDateTime arrivalDateTime,
                                                   int flightsIntervalInHours) {

        validateFlightsTimes(departureDateTime, arrivalDateTime);

        List<RouteDTO> routes = routeService.getFlightRoutes();
        List<ItineraryDTO> itineraries = new ArrayList<>();

        itineraries.addAll(getDirectItineraries(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime, routes));
        itineraries.addAll(getOneStopItineraries(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime, routes,
                flightsIntervalInHours));


        return itineraries;
    }

    //TODO: With N stops. Haven't yet finished
    @Override
    public List<ItineraryDTO> searchInterConnected(String departureAirport,
                                                   String arrivalAirport,
                                                   LocalDateTime departureDateTime,
                                                   LocalDateTime arrivalDateTime,
                                                   int flightsIntervalInHours,
                                                   int stopsCount) {

        validateFlightsTimes(departureDateTime, arrivalDateTime);

        List<RouteDTO> routes = routeService.getFlightRoutes();
        List<ItineraryDTO> itineraries = new ArrayList<>();

        itineraries.addAll(getItinerariesByStopCount(departureAirport, arrivalAirport, departureDateTime,
                arrivalDateTime, routes, flightsIntervalInHours, stopsCount));

        return itineraries;
    }


    private Collection<? extends ItineraryDTO> getItinerariesByStopCount(String departureAirport,
                                                                         String arrivalAirport,
                                                                         LocalDateTime departureDateTime,
                                                                         LocalDateTime arrivalDateTime,
                                                                         List<RouteDTO> routes,
                                                                         int flightsIntervalInHours,
                                                                         int stopsCount) {

        //TODO: Filtering by stops count haven't yet implemented. RouteFinder.findRoutes() returns one of existed connections
        // from departure to arrival airport. Those routes should be filtered by 'stopsCount`.
        return routeService.findRoutes(departureAirport, arrivalAirport, routes)
                .stream()
                .map(connection -> getItinerariesByConnection(departureDateTime, arrivalDateTime, connection,
                        flightsIntervalInHours))
                .flatMap(Collection::stream).collect(toList());
    }

    private void validateFlightsTimes(LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        if (departureDateTime.isAfter(arrivalDateTime)) {
            throw new IllegalArgumentException("Invalid departure or arrival time was given");
        }
    }
    
    private List<ItineraryDTO> getDirectItineraries(String departureAirport,
                                                    String arrivalAirport,
                                                    LocalDateTime departureDateTime,
                                                    LocalDateTime arrivalDateTime,
                                                    List<RouteDTO> routes) {

        if (!isDirectRouteExists(departureAirport, arrivalAirport, routes)) {
            return new ArrayList<>();
        }

        return schedulesService.getFlightsInPeriod(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime)
                .stream()
                .map((FlightSearchServiceUtil::createItinerary)).collect(toList());
    }

    private List<ItineraryDTO> getOneStopItineraries(String departureAirport,
                                                     String arrivalAirport,
                                                     LocalDateTime departureTime,
                                                     LocalDateTime arrivalTime,
                                                     List<RouteDTO> routes,
                                                     int flightsIntervalInHours) {

        return routeService.getOneStopConnections(departureAirport, arrivalAirport, routes)
                .stream()
                .map(connection -> getItinerariesByConnection(departureTime, arrivalTime, connection,
                        flightsIntervalInHours))
                .flatMap(Collection::stream).collect(toList());
    }

    private List<ItineraryDTO> getItinerariesByConnection(LocalDateTime departureTime,
                                                          LocalDateTime arrivalTime,
                                                          FlightConnectionDTO connection,
                                                          int flightsIntervalInHours) {

        RouteDTO firstRoute = connection.getFirstRoute();
        RouteDTO secondRoute = connection.getSecondRoute();

        List<FlightInfoDTO> firstFlights = schedulesService.getFlightsInPeriod(firstRoute.getAirportFrom(),
                firstRoute.getAirportTo(), departureTime, arrivalTime);

        List<FlightInfoDTO> secondFlights = schedulesService.getFlightsInPeriod(secondRoute.getAirportFrom(),
                secondRoute.getAirportTo(), departureTime, arrivalTime);

        return firstFlights
                .stream()
                .map(firstFlight -> getValidItineraries(firstFlight, secondFlights, flightsIntervalInHours))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<ItineraryDTO> getValidItineraries(FlightInfoDTO firstFlight,
                                                   List<FlightInfoDTO> secondFlights,
                                                   int flightsIntervalInHours) {
        return secondFlights
                .stream()
                .filter(secondFlight -> checkPeriodBetweenFlights(firstFlight.getArrivalTime(),
                        secondFlight.getDepartureTime(), flightsIntervalInHours))
                .map(secondFlight -> createItinerary(firstFlight, secondFlight))
                .collect(toList());
    }
}
