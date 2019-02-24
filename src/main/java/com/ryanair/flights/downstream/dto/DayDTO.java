package com.ryanair.flights.downstream.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DayDTO {

    private int day;

    private List<FlightDTO> flights;
}
