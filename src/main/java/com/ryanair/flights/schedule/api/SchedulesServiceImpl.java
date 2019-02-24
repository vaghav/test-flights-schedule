package com.ryanair.flights.schedule.api;

import com.ryanair.flights.internal.dto.FlightInfoDTO;
import com.ryanair.flights.schedule.api.dto.ScheduleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ryanair.flights.util.DateUtil.isFlightInPeriod;
import static com.ryanair.flights.util.FlightSearchServiceUtil.convert;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class SchedulesServiceImpl implements SchedulesService {

    private final RestTemplate restTemplate;

    @Autowired
    public SchedulesServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<FlightInfoDTO> getFlightsInPeriod(String departureAirport, String arrivalAirport,
                                                  LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        List<FlightInfoDTO> flights = new ArrayList<>();

        flights.addAll(getFlightsInfo(departureAirport, arrivalAirport, departureDateTime.getYear(),
                departureDateTime.getMonth()));

        Month tempMonth = departureDateTime.getMonth();
        int tempYear = departureDateTime.getYear();

        while ((arrivalDateTime.getMonth() != tempMonth || arrivalDateTime.getYear() != tempYear)) {
            tempMonth = tempMonth.plus(1);
            if (tempMonth == Month.JANUARY) {
                tempYear++;
            }

            flights.addAll(getFlightsInfo(departureAirport, arrivalAirport, tempYear, tempMonth));
        }

        return flights
                .stream()
                .filter(schedule -> isFlightInPeriod(departureDateTime, arrivalDateTime, schedule))
                .collect(toList());
    }

    private List<FlightInfoDTO> getFlightsInfo(String departureAirport, String arrivalAirport,
                                               int year, Month month) {

        Optional<ScheduleDTO> flightsSchedule = getSchedules(departureAirport, arrivalAirport,
                year, month.getValue());

        if (!flightsSchedule.map(ScheduleDTO::getDays).isPresent()) {
            return Collections.emptyList();
        }
        return convert(flightsSchedule.get(), year, departureAirport, arrivalAirport);
    }

    private Optional<ScheduleDTO> getSchedules(String departureAirport, String arrivalAirport, int year, int month) {

        String URL = String.format("https://services-api.ryanair.com/timtbl/3/schedules/%s/%s/years/%d/months/%d",
                departureAirport, arrivalAirport, year, month);
        try {
            return Optional.ofNullable(restTemplate.getForObject(URI.create(URL), ScheduleDTO.class));
        } catch (RuntimeException ex) {
            log.error("Could't get flight schedule ");
            return Optional.empty();
        }
    }
}
