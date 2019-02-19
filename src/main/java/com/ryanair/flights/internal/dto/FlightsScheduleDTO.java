package com.ryanair.flights.internal.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class FlightsScheduleDTO {

    private int year;

    private String airportFrom;

    private String airportTo;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;
}
