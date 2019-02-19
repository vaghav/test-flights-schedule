package com.ryanair.flights.internal.downstream.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyFlightsScheduleDTO {

    private int day;

    private List<FlightDTO> flights;
}
