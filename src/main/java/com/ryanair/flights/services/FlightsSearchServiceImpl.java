package com.ryanair.flights.services;

import com.ryanair.flights.dto.*;
import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import com.ryanair.flights.internal.downstream.dto.ScheduleDTO;
import com.ryanair.flights.internal.dto.*;
import com.ryanair.flights.util.FlightSearchServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static com.ryanair.flights.util.DateUtil.*;
import static com.ryanair.flights.util.FlightSearchServiceUtil.*;
import static com.ryanair.flights.util.RouteUtil.getOneStopConnections;
import static com.ryanair.flights.util.RouteUtil.isDirectRouteExists;
import static java.util.stream.Collectors.toList;

@Service
public class FlightsSearchServiceImpl implements FlightsSearchService {

    @Value("${flights.interval:2}")
    private Integer flightsIntervalInHours;

    @Value("${flights.stops.count:2}")
    private Integer flightsStopsCount;

    private final RouteService routeService;

    private final SchedulesService schedulesService;

    private final RouteFinder routeFinder;

    @Autowired
    public FlightsSearchServiceImpl(RouteService routeService, SchedulesService schedulesService,
                                    RouteFinder routeFinder) {
        this.routeService = routeService;
        this.schedulesService = schedulesService;
        this.routeFinder = routeFinder;
    }

    @Override
    public List<ItineraryDTO> searchInterConnected(String departureAirport,
                                                   String arrivalAirport,
                                                   LocalDateTime departureTime,
                                                   LocalDateTime arrivalTime) {
        return searchInterConnected(departureAirport, arrivalAirport, departureTime, arrivalTime,
                flightsIntervalInHours);
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

    private void validateFlightsTimes(LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        if (departureDateTime.isAfter(arrivalDateTime)) {
            throw new IllegalArgumentException("Invalid departure or arrival time was given");
        }
    }

    private Collection<? extends ItineraryDTO> getItinerariesByStopCount(String departureAirport,
                                                                         String arrivalAirport,
                                                                         LocalDateTime departureDateTime,
                                                                         LocalDateTime arrivalDateTime,
                                                                         List<RouteDTO> routes,
                                                                         int flightsIntervalInHours, int stopsCount) {

        //TODO: Filtering by stops count haven't yet implemented. RouteFinder.findRoutes() returns some existed route
        return routeFinder.findRoutes(departureAirport, arrivalAirport, routes).stream()
                .map(connection -> getItinerariesByConnection(departureDateTime, arrivalDateTime, connection,
                        flightsIntervalInHours))
                .flatMap(Collection::stream).collect(toList());
    }
    
    private List<ItineraryDTO> getDirectItineraries(String departureAirport,
                                                    String arrivalAirport,
                                                    LocalDateTime departureDateTime,
                                                    LocalDateTime arrivalDateTime,
                                                    List<RouteDTO> routes) {

        if (!isDirectRouteExists(departureAirport, arrivalAirport, routes)) {
            return new ArrayList<>();
        }

        return getFlightsScheduleInPeriod(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime).stream()
                .map((FlightSearchServiceUtil::createItinerary)).collect(toList());
    }

    private List<FlightsScheduleDTO> getFlightsScheduleInPeriod(String departureAirport,
                                                                String arrivalAirport,
                                                                LocalDateTime departureDateTime,
                                                                LocalDateTime arrivalDateTime) {

        List<FlightsScheduleDTO> flightsSchedules = new ArrayList<>();

        flightsSchedules.addAll(getFlightSchedules(departureAirport, arrivalAirport, departureDateTime.getYear(),
                departureDateTime.getMonth()));

        Month tempMonth = departureDateTime.getMonth();
        int tempYear = departureDateTime.getYear();

        while ((arrivalDateTime.getMonth() != tempMonth || arrivalDateTime.getYear() != tempYear)) {
            tempMonth = tempMonth.plus(1);
            if (tempMonth == Month.JANUARY) {
                tempYear++;
            }

            flightsSchedules.addAll(getFlightSchedules(departureAirport, arrivalAirport, tempYear, tempMonth));
        }

        return flightsSchedules
                .stream()
                .filter(schedule -> isFlightInPeriod(departureDateTime, arrivalDateTime, schedule))
                .collect(toList());
    }

    private List<FlightsScheduleDTO> getFlightSchedules(String departureAirport,
                                                        String arrivalAirport,
                                                        int year, Month month) {

        Optional<ScheduleDTO> flightsSchedule = schedulesService.getFlightsSchedule(departureAirport, arrivalAirport,
                year, month.getValue());

        if (!flightsSchedule.map(ScheduleDTO::getDays).isPresent()){
            return Collections.emptyList();
        }
        return convert(flightsSchedule.get(), year, departureAirport, arrivalAirport);
    }

    private List<ItineraryDTO> getOneStopItineraries(String departureAirport,
                                                     String arrivalAirport,
                                                     LocalDateTime departureTime,
                                                     LocalDateTime arrivalTime,
                                                     List<RouteDTO> routes,
                                                     int flightsIntervalInHours) {

        return getOneStopConnections(departureAirport, arrivalAirport, routes).stream()
                .map(connection -> getItinerariesByConnection(departureTime, arrivalTime, connection, flightsIntervalInHours))
                .flatMap(Collection::stream).collect(toList());
    }

    private List<ItineraryDTO> getItinerariesByConnection(LocalDateTime departureTime,
                                                          LocalDateTime arrivalTime,
                                                          FlightConnectionDTO connection,
                                                          int flightsIntervalInHours) {

        RouteDTO firstRoute = connection.getFirstRoute();
        RouteDTO secondRoute = connection.getSecondRoute();

        List<FlightsScheduleDTO> firstFlights = getFlightsScheduleInPeriod(firstRoute.getAirportFrom(),
                firstRoute.getAirportTo(), departureTime, arrivalTime);

        List<FlightsScheduleDTO> secondFlights = getFlightsScheduleInPeriod(secondRoute.getAirportFrom(),
                secondRoute.getAirportTo(), departureTime, arrivalTime);

        return firstFlights
                .stream()
                .map(firstFlight -> getValidItineraries(firstFlight, secondFlights, flightsIntervalInHours))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<ItineraryDTO> getValidItineraries(FlightsScheduleDTO firstFlight,
                                                   List<FlightsScheduleDTO> secondFlights,
                                                   int flightsIntervalInHours) {
        return secondFlights
                .stream()
                .filter(secondFlight -> checkPeriodBetweenFlights(firstFlight.getArrivalTime(),
                        secondFlight.getDepartureTime(), flightsIntervalInHours))
                .map(secondFlight -> createItinerary(firstFlight, secondFlight))
                .collect(toList());
    }
}
