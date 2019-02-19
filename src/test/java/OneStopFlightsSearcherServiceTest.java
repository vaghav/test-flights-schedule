import com.ryanair.flights.dto.ItineraryDTO;
import com.ryanair.flights.dto.LegDTO;
import com.ryanair.flights.internal.downstream.dto.DailyFlightsScheduleDTO;
import com.ryanair.flights.internal.downstream.dto.FlightDTO;
import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import com.ryanair.flights.internal.downstream.dto.ScheduleDTO;
import junitparams.Parameters;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class OneStopFlightsSearcherServiceTest extends CommonFlightSearcherServiceTest {

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(searcherService, "flightsIntervalInHours", 2, Integer.class);
    }

    @Test
    @Parameters(method = "parametersForOneStopFlights")
    public void shouldReturnInterConnectedFlights(Optional<ScheduleDTO> flightsScheduleFirst,
                                                  Optional<ScheduleDTO> flightsScheduleSecond,
                                                  List<RouteDTO> routes,
                                                  List<ItineraryDTO> expectedFlights) {
        //given
        prepareStubs(flightsScheduleFirst, flightsScheduleSecond, routes);
        when(schedulesServiceMock.getFlightsSchedule(eq("WRO"), eq("GRO"), anyShort(), anyByte()))
                .thenReturn(flightsScheduleFirst);
        when(schedulesServiceMock.getFlightsSchedule(eq("GRO"), eq("WAW"), anyShort(), anyByte()))
                .thenReturn(flightsScheduleSecond);

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime, arrivalDateTime, 2);

        //then
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expectedFlights, actualFlights));
    }

    @Test
    public void shouldReturnInterEmptyFlightsArrivalAndDepartureTimeNotInRange() {
        //given
        prepareStubs(createFirstFlightsSchedule(), createSecondFlightSchedule(), createSingleConnection());

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime.plusMinutes(16), arrivalDateTime.minusHours(1), 2);

        //then
        Assert.assertTrue(EqualsBuilder.reflectionEquals(new ArrayList<>(), actualFlights));
    }

    @Test
    public void shouldReturnInterEmptyFlightsArrivalTimeNotInRange() {
        //given
        prepareStubs(createFirstFlightsSchedule(), createSecondFlightSchedule(), createSingleConnection());

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime, arrivalDateTime.minusHours(2), 2);

        //then
        Assert.assertTrue(EqualsBuilder.reflectionEquals(new ArrayList<>(), actualFlights));
    }

    @Test
    public void shouldReturnInterEmptyFlightsDepartureTimeNotInRange() {
        //given
        prepareStubs(createFirstFlightsSchedule(), createSecondFlightSchedule(), createSingleConnection());

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime.plusMinutes(16), arrivalDateTime);

        //then
        Assert.assertTrue(EqualsBuilder.reflectionEquals(new ArrayList<>(), actualFlights));
    }

    @Test
    public void shouldReturnInterEmptyFlightsInvalidPeriod() {
        //given
        prepareStubs(createFirstFlightsSchedule(), createSecondFlightSchedule(), createSingleConnection());

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime, arrivalDateTime, 4);

        //then
        Assert.assertTrue(EqualsBuilder.reflectionEquals(new ArrayList<>(), actualFlights));
    }

    @Test
    public void shouldReturnInterEmptyFlightsNoConnection() {
        //given
        prepareStubs(createFirstFlightsSchedule(), createSecondFlightSchedule(), createSingleConnection());

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "EVN",
                departureDateTime, arrivalDateTime, 2);

        //then
        Assert.assertTrue(EqualsBuilder.reflectionEquals(new ArrayList<>(), actualFlights));
    }

    private void prepareStubs(Optional<ScheduleDTO> flightsScheduleFirst, Optional<ScheduleDTO> flightsScheduleSecond,
                                List<RouteDTO> routes) {
        when(routeServiceMock.getFlightRoutes()).thenReturn(routes);
        when(schedulesServiceMock.getFlightsSchedule(eq("WRO"), eq("SVO"), anyShort(), anyByte()))
                .thenReturn(flightsScheduleFirst);
        when(schedulesServiceMock.getFlightsSchedule(eq("SVO"), eq("WAW"), anyShort(), anyByte()))
                .thenReturn(flightsScheduleSecond);
    }

    private Object[] parametersForOneStopFlights() {
        return new Object[][]{{createFirstFlightsSchedule(), createSecondFlightSchedule(), createSingleConnection(),
                createSingleExpectedFlight()}, {createFirstFlightsSchedule(), createSecondFlightSchedule(),
                createSeveralConnections(), createTwoExpectedFlights()}};
    }

    private List<RouteDTO> createSingleConnection() {
        return List.of(new RouteDTO("WRO", "SVO"),
                new RouteDTO("SVO", "WAW"));
    }

    private List<RouteDTO> createSeveralConnections() {
        return List.of(new RouteDTO("WRO", "GRO"),
                new RouteDTO("WAW", "WRO"),
                new RouteDTO("GRO", "WAW"), new RouteDTO("WRO", "BCN"),
                new RouteDTO("BCN", "SVO"), new RouteDTO("WRO", "SVO"),
                new RouteDTO("WAW", "SVO"), new RouteDTO("WRO", "LIS"),
                new RouteDTO("WRO", "SVO"), new RouteDTO("SVO", "WAW"),
                new RouteDTO("MAD", "WAW"), new RouteDTO("MAD", "BCN"));
    }

    private Optional<ScheduleDTO> createSecondFlightSchedule() {
        return Optional.of(new ScheduleDTO(3, List.of(new DailyFlightsScheduleDTO(2,
                List.of(new FlightDTO( "FL45", LocalTime.parse("15:34"), LocalTime.parse("21:09")))))));
    }

    private Optional<ScheduleDTO> createFirstFlightsSchedule() {
        return Optional.of(new ScheduleDTO(3, List.of(new DailyFlightsScheduleDTO(2,
                List.of(new FlightDTO("FL45", LocalTime.parse("07:15"), LocalTime.parse("11:35")))))));
    }

    private List<ItineraryDTO> createTwoExpectedFlights() {
        return new ArrayList<>(Arrays.asList(createItinerary(
                new LegDTO("WRO", "GRO",
                        LocalDateTime.parse("2016-03-02T07:15"), LocalDateTime.parse("2016-03-02T11:35")),
                new LegDTO("GRO", "WAW", LocalDateTime.parse("2016-03-02T15:34"),
                        LocalDateTime.parse("2016-03-02T21:09"))),
                createItinerary(new LegDTO("WRO", "SVO",
                                LocalDateTime.parse("2016-03-02T07:15"), LocalDateTime.parse("2016-03-02T11:35")),
                        new LegDTO("SVO", "WAW",
                                LocalDateTime.parse("2016-03-02T15:34"), LocalDateTime.parse("2016-03-02T21:09")))));
    }

    private List<ItineraryDTO> createSingleExpectedFlight() {
        return new ArrayList<>(Arrays.asList(createItinerary(
                new LegDTO("WRO", "SVO",
                        LocalDateTime.parse("2016-03-02T07:15"), LocalDateTime.parse("2016-03-02T11:35")),
                new LegDTO("SVO", "WAW",
                        LocalDateTime.parse("2016-03-02T15:34"), LocalDateTime.parse("2016-03-02T21:09")))));
    }

}
