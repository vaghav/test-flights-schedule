package com.ryanair.flights.util;

import com.ryanair.flights.schedule.api.dto.FlightDTO;
import com.ryanair.flights.internal.dto.FlightInfoDTO;
import com.ryanair.flights.schedule.api.dto.ScheduleDTO;
import com.ryanair.flights.services.dto.ItineraryDTO;
import com.ryanair.flights.services.dto.LegDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class FlightSearchServiceUtil {

    public static List<FlightInfoDTO> convert(ScheduleDTO schedule, int year,
                                              String departAirport, String arrivalAirport) {

        return schedule.getDays().stream()
                .flatMap(flightsSchedule -> flightsSchedule.getFlights().stream()
                        .map(flight -> createFlightInfo(year, schedule.getMonth(), flightsSchedule.getDay(),
                                departAirport, arrivalAirport, flight))).collect(toList());
    }

    private static FlightInfoDTO createFlightInfo(int year, int month, int day, String departAirport,
                                                  String arrivalAirport, FlightDTO flight) {

        return new FlightInfoDTO(departAirport, arrivalAirport,
                LocalDateTime.of(year, month, day, flight.getDepartureTime().getHour(),
                        flight.getDepartureTime().getMinute()),
                LocalDateTime.of(year, month, day, flight.getArrivalTime().getHour(),
                        flight.getArrivalTime().getMinute()));
    }

    public static ItineraryDTO createItinerary(FlightInfoDTO... flightInfo) {
        return new ItineraryDTO(
                Arrays.stream(flightInfo).map(FlightSearchServiceUtil::createLeg).collect(toList()));
    }

    private static LegDTO createLeg(FlightInfoDTO flightInfo) {
        return new LegDTO(flightInfo.getAirportFrom(), flightInfo.getAirportTo(),
                flightInfo.getDepartureTime(), flightInfo.getArrivalTime());
    }
}
