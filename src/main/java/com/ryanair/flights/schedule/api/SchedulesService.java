package com.ryanair.flights.schedule.api;

import com.ryanair.flights.internal.dto.FlightInfoDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulesService {

    /**
     * Get collection of flights for given departure and arrival airports within departure and arrival time.
     *
     * Using Schedules API for getting flight schedules by year and month.
     * https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}
     * Example:
     * https://services-api.ryanair.com/timtbl/3/schedules/DUB/WRO/years/2019/months/6:

     * @param departureAirport
     * @param arrivalAirport
     * @param departureDateTime
     * @param arrivalDateTime
     * @return
     */
    List<FlightInfoDTO> getFlightsInPeriod(String departureAirport, String arrivalAirport,
                                           LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);
}
