package com.ryanair.flights.web;

import com.ryanair.flights.dto.ItineraryDTO;
import com.ryanair.flights.services.FlightsSearchService;
import com.ryanair.flights.services.FlightsSearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class FlightsController {

    private final FlightsSearchService flightsSearcherService;

    @Autowired
    public FlightsController(FlightsSearchServiceImpl flightsSearcherService) {
        this.flightsSearcherService = flightsSearcherService;
    }

	/**
	 * Return interconnected flights with given parameters. Example of end-point
	 * http://localhost:8080/interconnections?departure=DUB&arrival=TSF&departureDateTime=2017-03-02T07:00&arrivalDateTime=2017-03-02T21:00
	 * @param departure
	 * @param arrival
	 * @param departureDateTime
	 * @param arrivalDateTime
	 * @return
	 */
	@RequestMapping(value = "/interconnections", method = RequestMethod.GET)
    public ResponseEntity<List<ItineraryDTO>> findFlights(@RequestParam("departure") String departure,
                                                          @RequestParam("arrival") String arrival,
                                                          @RequestParam("departureDateTime") String departureDateTime,
                                                          @RequestParam("arrivalDateTime") String arrivalDateTime) {

		LocalDateTime departureTime = LocalDateTime.parse(departureDateTime);
		LocalDateTime arrivalTime = LocalDateTime.parse(arrivalDateTime);
        List<ItineraryDTO> flights = flightsSearcherService.searchInterConnected(departure, arrival,
				departureTime, arrivalTime);
        return new ResponseEntity<>(flights, HttpStatus.OK);
	}
}
