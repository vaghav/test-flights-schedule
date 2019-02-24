package com.ryanair.flights.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FlightPathDTO {

    private List<FlightConnectionDTO> connectionList;
}
