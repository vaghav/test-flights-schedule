package com.ryanair.flights.schedule.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    private int month;

    private List<DayDTO> days;
}
