package com.ryanair.flights.services.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ItineraryDTO {

    private List<LegDTO> legs;

    public int getStops() {
        return legs.size() - 1;
    }

    public List<LegDTO> getLegs() {
        return legs;
    }
}
