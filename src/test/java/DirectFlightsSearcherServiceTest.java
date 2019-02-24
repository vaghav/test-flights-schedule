import com.ryanair.flights.route.api.dto.RouteDTO;
import com.ryanair.flights.services.dto.ItineraryDTO;
import com.ryanair.flights.services.dto.LegDTO;
import com.ryanair.flights.internal.dto.FlightInfoDTO;
import junitparams.Parameters;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class DirectFlightsSearcherServiceTest extends CommonFlightSearcherServiceTest {

    @Test
    @Parameters(method = "parametersForDirectFlights")
    public void shouldReturnDirectFlights(List<FlightInfoDTO> flightsInfo,
                                          List<RouteDTO> routes, List<ItineraryDTO> expectedFlights) {
        //given
        prepareStubs(flightsInfo, routes);

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime, arrivalDateTime, 2);

        //then
        assertThat(actualFlights).isEqualToComparingFieldByFieldRecursively(expectedFlights);
    }

    @Test
    @Parameters(method = "parametersForDirectFlights")
    public void shouldThrowExceptionWhenDatesAreInvalid(List<FlightInfoDTO> flightsScheduleFirst,
                                          List<RouteDTO> routes, List<ItineraryDTO> expectedFlights) {
        //given
        prepareStubs(flightsScheduleFirst, routes);

        //when-then
        assertThatThrownBy(() -> searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime.plusYears(1), arrivalDateTime, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid departure or arrival time was given");
    }

    private void prepareStubs(List<FlightInfoDTO> flightsInfo, List<RouteDTO> routes) {
        when(routeServiceMock.getFlightRoutes()).thenReturn(routes);
        when(schedulesServiceMock.getFlightsInPeriod(eq("WRO"), eq("WAW"), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(flightsInfo);
    }

    protected Object[] parametersForDirectFlights() {
        return new Object[][]{{
                List.of(new FlightInfoDTO("WRO", "WAW", LocalDateTime.parse("2019-03-02T07:15"),
                        LocalDateTime.parse("2019-03-02T11:35"))),
                List.of(new RouteDTO("WRO", "WAW")),
                new ArrayList<>(List.of(createItinerary(
                        new LegDTO("WRO", "WAW",
                                LocalDateTime.parse("2019-03-02T07:15"), LocalDateTime.parse("2019-03-02T11:35")))))},
        };
    }
}
