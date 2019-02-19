package com.ryanair.flights.internal.dto;

import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import lombok.Getter;

@Getter
public class FlightConnectionDTO {

    private RouteDTO firstRoute;

    private RouteDTO secondRoute;

    public FlightConnectionDTO(RouteDTO firstRoute, RouteDTO secondRoute) {
        if (!firstRoute.getAirportTo().equals(secondRoute.getAirportFrom())) {
            throw new IllegalArgumentException("First flight arrival airport is different from second " +
                    "flight departure airport");
        }
        this.firstRoute = firstRoute;
        this.secondRoute = secondRoute;
    }
}
