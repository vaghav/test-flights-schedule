package com.ryanair.flights.util;

import com.ryanair.flights.dto.*;
import com.ryanair.flights.internal.downstream.dto.FlightDTO;
import com.ryanair.flights.internal.dto.FlightsScheduleDTO;
import com.ryanair.flights.internal.downstream.dto.ScheduleDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class FlightSearchServiceUtil {

    public static List<FlightsScheduleDTO> convert(ScheduleDTO schedule,
                                                   int year,
                                                   String departAirport,
                                                   String arrivalAirport) {

        return schedule.getDays().stream()
                .flatMap(flightsSchedule -> flightsSchedule.getFlights().stream()
                        .map(flight -> createFlightSchedule(year, schedule.getMonth(), flightsSchedule.getDay(),
                                departAirport, arrivalAirport, flight))).collect(toList());
    }

    private static FlightsScheduleDTO createFlightSchedule(int year, int month,
                                                           int day,
                                                           String departAirport,
                                                           String arrivalAirport,
                                                           FlightDTO flight) {

        return new FlightsScheduleDTO(year, departAirport, arrivalAirport,
                LocalDateTime.of(year, month, day, flight.getDepartureTime().getHour(),
                        flight.getDepartureTime().getMinute()),
                LocalDateTime.of(year, month, day, flight.getArrivalTime().getHour(),
                        flight.getArrivalTime().getMinute()));
    }

    public static ItineraryDTO createItinerary(FlightsScheduleDTO... flightSchedule) {
        return new ItineraryDTO(Arrays.stream(flightSchedule)
                .map(flightsSchedule -> createLeg(flightsSchedule)).collect(toList()));
    }

    private static LegDTO createLeg(FlightsScheduleDTO flightSchedule) {

        return new LegDTO(flightSchedule.getAirportFrom(), flightSchedule.getAirportTo(),
                flightSchedule.getDepartureTime(), flightSchedule.getArrivalTime());
    }
}
