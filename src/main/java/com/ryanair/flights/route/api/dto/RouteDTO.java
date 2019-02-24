package com.ryanair.flights.route.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class RouteDTO {

    private String airportFrom;

    private String airportTo;

    private String connectingAirport;

    private String operator;

    private boolean newRoute;

    private boolean seasonalRoute;

    private String group;

    public RouteDTO(String airportFrom, String airportTo) {
        this.airportFrom = airportFrom;
        this.airportTo = airportTo;
    }
}
