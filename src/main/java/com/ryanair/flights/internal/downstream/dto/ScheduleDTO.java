package com.ryanair.flights.internal.downstream.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    private int month;

    private List<DailyFlightsScheduleDTO> days;
}
