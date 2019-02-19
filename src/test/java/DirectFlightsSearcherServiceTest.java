import com.ryanair.flights.dto.ItineraryDTO;
import com.ryanair.flights.dto.LegDTO;
import com.ryanair.flights.internal.downstream.dto.DailyFlightsScheduleDTO;
import com.ryanair.flights.internal.downstream.dto.FlightDTO;
import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import com.ryanair.flights.internal.downstream.dto.ScheduleDTO;
import junitparams.Parameters;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


public class DirectFlightsSearcherServiceTest extends CommonFlightSearcherServiceTest {

    @Test
    @Parameters(method = "parametersForDirectFlights")
    public void shouldReturnDirectFlights(Optional<ScheduleDTO> flightsScheduleFirst,
                                          List<RouteDTO> routes, List<ItineraryDTO> expectedFlights) {
        //given
        prepareStubs(flightsScheduleFirst, routes);

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime, arrivalDateTime, 2);

        //then
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expectedFlights, actualFlights));
    }

    @Test
    @Parameters(method = "parametersForDirectFlights")
    public void shouldThrowExceptionWhenDatesAreInvalid(Optional<ScheduleDTO> flightsScheduleFirst,
                                          List<RouteDTO> routes, List<ItineraryDTO> expectedFlights) {
        //given
        prepareStubs(flightsScheduleFirst, routes);

        //when-then
        assertThatThrownBy(() -> searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime.plusYears(1), arrivalDateTime, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid departure or arrival time was given");
    }

    private void prepareStubs(Optional<ScheduleDTO> flightsScheduleFirst, List<RouteDTO> routes) {
        when(routeServiceMock.getFlightRoutes()).thenReturn(routes);
        when(schedulesServiceMock.getFlightsSchedule(eq("WRO"), eq("WAW"), anyShort(), anyByte()))
                .thenReturn(flightsScheduleFirst);
    }

    protected Object[] parametersForDirectFlights() {
        return new Object[][]{{
                Optional.of(new ScheduleDTO(3, List.of(new DailyFlightsScheduleDTO(2,
                        List.of(new FlightDTO("FL45", LocalTime.parse("07:15"), LocalTime.parse("11:09"))))))),
                List.of(new RouteDTO("WRO", "WAW")),
                new ArrayList<>(Arrays.asList(createItinerary(
                        new LegDTO("WRO", "WAW",
                                LocalDateTime.parse("2016-03-02T07:15"), LocalDateTime.parse("2016-03-02T11:35")))))},
        };
    }
}
