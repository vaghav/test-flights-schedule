package com.ryanair.flights.internal.downstream.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RouteDTO {

    private String airportFrom;

    private String airportTo;

    private boolean newRoute;

    private boolean seasonalRoute;

    public RouteDTO(String airportFrom, String airportTo) {
        this.airportFrom = airportFrom;
        this.airportTo = airportTo;
    }
}
