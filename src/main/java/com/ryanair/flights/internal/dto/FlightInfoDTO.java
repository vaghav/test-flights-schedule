package com.ryanair.flights.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class FlightInfoDTO {

    private String airportFrom;

    private String airportTo;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;
}
