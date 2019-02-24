import com.ryanair.flights.route.api.dto.RouteDTO;
import com.ryanair.flights.services.dto.ItineraryDTO;
import com.ryanair.flights.services.dto.LegDTO;
import com.ryanair.flights.internal.dto.FlightConnectionDTO;
import com.ryanair.flights.internal.dto.FlightInfoDTO;
import junitparams.Parameters;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class OneStopFlightsSearcherServiceTest extends CommonFlightSearcherServiceTest {

    @Test
    @Parameters(method = "parametersForOneStopFlights")
    public void shouldReturnInterConnectedFlights(List<FlightInfoDTO> flightsInfoFirst,
                                                  List<FlightInfoDTO> flightsInfoSecond,
                                                  List<RouteDTO> routes,
                                                  List<FlightConnectionDTO> flightConnections,
                                                  List<ItineraryDTO> expectedFlights) {
        //given
        prepareStubs(flightsInfoFirst, flightsInfoSecond, routes, flightConnections);
        when(schedulesServiceMock.getFlightsInPeriod(eq("WRO"), eq("GRO"), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(List.of(new FlightInfoDTO("WRO","GRO",
                LocalDateTime.parse("2019-03-02T07:15"), LocalDateTime.parse("2019-03-02T11:35"))));
        when(schedulesServiceMock.getFlightsInPeriod(eq("GRO"), eq("WAW"), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(List.of(new FlightInfoDTO( "GRO", "WAW",
                LocalDateTime.parse("2019-03-02T15:34"), LocalDateTime.parse("2019-03-02T21:09"))));

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime, arrivalDateTime, 2);

        //then
        assertThat(actualFlights).isEqualToComparingFieldByFieldRecursively(expectedFlights);
    }

    @Test
    public void shouldReturnInterEmptyFlightsInvalidPeriod() {
        //given
        prepareStubs(createFirstFlightsInfo(), createSecondFlightInfo(), createFirstRoutes(), createFlightConnection());

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime, arrivalDateTime, 4);

        //then
        assertThat(actualFlights).isEqualToComparingFieldByFieldRecursively(new ArrayList<>());
    }

    @Test
    public void shouldReturnInterEmptyFlightsNoConnection() {
        //given
        prepareStubs(createFirstFlightsInfo(), createSecondFlightInfo(), createFirstRoutes(), createFlightConnection());


        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "EVN",
                departureDateTime, arrivalDateTime, 2);

        //then
        assertThat(actualFlights).isEqualToComparingFieldByFieldRecursively(new ArrayList<>());
    }

    private void prepareStubs(List<FlightInfoDTO> flightsInfoFirst, List<FlightInfoDTO> flightsInfoSecond,
                              List<RouteDTO> routes, List<FlightConnectionDTO> flightConnections) {

        when(routeServiceMock.getFlightRoutes()).thenReturn(routes);
        when(routeServiceMock.getOneStopConnections("WRO", "WAW", routes))
                .thenReturn(flightConnections);
        when(schedulesServiceMock.getFlightsInPeriod(eq("WRO"), eq("SVO"), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(flightsInfoFirst);
        when(schedulesServiceMock.getFlightsInPeriod(eq("SVO"), eq("WAW"), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(flightsInfoSecond);
    }

    private Object[] parametersForOneStopFlights() {
        return new Object[][]
                {
                        {
                                createFirstFlightsInfo(), createSecondFlightInfo(),
                                createFirstRoutes(),
                                createFlightConnection(),
                                createSingleExpectedFlight()
                        },
                        {
                                createFirstFlightsInfo(), createSecondFlightInfo(),
                                createSecondRoutes(), List.of(
                                new FlightConnectionDTO(
                                        new RouteDTO("WRO", "GRO"),
                                        new RouteDTO("GRO", "WAW")),
                                new FlightConnectionDTO(
                                        new RouteDTO("WRO", "SVO"),
                                        new RouteDTO("SVO", "WAW"))),
                                createTwoExpectedFlights()
                        }
                };
    }

    private List<FlightConnectionDTO> createFlightConnection() {
        return List.of(new FlightConnectionDTO(
                new RouteDTO("WRO", "SVO"),
                new RouteDTO("SVO", "WAW")));
    }

    private List<RouteDTO> createFirstRoutes() {
        return List.of(new RouteDTO("WRO", "SVO"),
                new RouteDTO("SVO", "WAW"));
    }

    private List<RouteDTO> createSecondRoutes() {
        return List.of(new RouteDTO("WRO", "GRO"),
                new RouteDTO("WAW", "WRO"),
                new RouteDTO("GRO", "WAW"), new RouteDTO("WRO", "BCN"),
                new RouteDTO("BCN", "SVO"), new RouteDTO("WRO", "SVO"),
                new RouteDTO("WAW", "SVO"), new RouteDTO("WRO", "LIS"),
                new RouteDTO("WRO", "SVO"), new RouteDTO("SVO", "WAW"),
                new RouteDTO("MAD", "WAW"), new RouteDTO("MAD", "BCN"));
    }

    private List<FlightInfoDTO> createSecondFlightInfo() {
        return List.of(new FlightInfoDTO( "SVO", "WAW",
                LocalDateTime.parse("2019-03-02T15:34"), LocalDateTime.parse("2019-03-02T21:09")));
    }

    private List<FlightInfoDTO> createFirstFlightsInfo() {
        return List.of(new FlightInfoDTO("WRO","SVO",
                LocalDateTime.parse("2019-03-02T07:15"), LocalDateTime.parse("2019-03-02T11:35")));
    }

    private List<ItineraryDTO> createTwoExpectedFlights() {
        return new ArrayList<>(List.of(createItinerary(
                new LegDTO("WRO", "GRO",
                        LocalDateTime.parse("2019-03-02T07:15"), LocalDateTime.parse("2019-03-02T11:35")),
                new LegDTO("GRO", "WAW", LocalDateTime.parse("2019-03-02T15:34"),
                        LocalDateTime.parse("2019-03-02T21:09"))),
                createItinerary(new LegDTO("WRO", "SVO",
                                LocalDateTime.parse("2019-03-02T07:15"), LocalDateTime.parse("2019-03-02T11:35")),
                        new LegDTO("SVO", "WAW",
                                LocalDateTime.parse("2019-03-02T15:34"), LocalDateTime.parse("2019-03-02T21:09")))));
    }

    private List<ItineraryDTO> createSingleExpectedFlight() {
        return new ArrayList<>(List.of(createItinerary(
                new LegDTO("WRO", "SVO",
                        LocalDateTime.parse("2019-03-02T07:15"), LocalDateTime.parse("2019-03-02T11:35")),
                new LegDTO("SVO", "WAW",
                        LocalDateTime.parse("2019-03-02T15:34"), LocalDateTime.parse("2019-03-02T21:09")))));
    }
}
