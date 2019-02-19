package com.ryanair.flights.util;

import com.ryanair.flights.dto.LegDTO;
import com.ryanair.flights.internal.dto.FlightsScheduleDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class DateUtil {

    public static boolean isFlightInPeriod(LocalDateTime from,
                                           LocalDateTime to,
                                           FlightsScheduleDTO schedule) {

        LocalDateTime departureTime = schedule.getDepartureTime();
        LocalDateTime arrivalTime = schedule.getArrivalTime();
        return (departureTime.isAfter(from) || departureTime.equals(from))
                && (arrivalTime.isBefore(to) || arrivalTime.equals(to));
    }

    public static boolean checkPeriodBetweenFlights(LocalDateTime arrivalTime,
                                                    LocalDateTime departureTime,
                                                    double flightsInterval) {

        return Math.floor(ChronoUnit.MINUTES.between(arrivalTime, departureTime) / 60d * 100) / 100 >= flightsInterval;
    }
}
