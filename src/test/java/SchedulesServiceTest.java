import com.ryanair.flights.services.dto.ItineraryDTO;
import com.ryanair.flights.internal.dto.FlightInfoDTO;
import com.ryanair.flights.schedule.api.dto.DayDTO;
import com.ryanair.flights.schedule.api.dto.FlightDTO;
import com.ryanair.flights.schedule.api.dto.ScheduleDTO;
import com.ryanair.flights.schedule.api.SchedulesServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class SchedulesServiceTest extends CommonFlightSearcherServiceTest {

    private static final String URL = "https://services-api.ryanair.com/timtbl/3/schedules/WRO/WAW/years/2019/months/3";

    @Mock
    private RestTemplate restTemplateMock;

    @InjectMocks
    private SchedulesServiceImpl schedulesService;

    @Test
    public void shouldReturnInterEmptyFlightsArrivalAndDepartureTimeNotInRange() {
        //given
        prepareStubs();

        //when
        List<FlightInfoDTO> actualFlights = schedulesService.getFlightsInPeriod("WRO", "WAW",
                departureDateTime.plusMinutes(16), arrivalDateTime.minusHours(1));

        //then
        assertThat(actualFlights).isEqualToComparingFieldByFieldRecursively(new ArrayList<>());
    }

    @Test
    public void shouldReturnInterEmptyFlightsArrivalTimeNotInRange() {
        //given
        prepareStubs();

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime, arrivalDateTime.minusHours(2), 2);

        //then
        assertThat(actualFlights).isEqualToComparingFieldByFieldRecursively(new ArrayList<>());
    }

    @Test
    public void shouldReturnInterEmptyFlightsDepartureTimeNotInRange() {
        //given
        prepareStubs();

        //when
        List<ItineraryDTO> actualFlights = searcherService.searchInterConnected("WRO", "WAW",
                departureDateTime.plusMinutes(16), arrivalDateTime);

        //then
        assertThat(actualFlights).isEqualToComparingFieldByFieldRecursively(new ArrayList<>());
    }

    private void prepareStubs() {
        ScheduleDTO schedule = new ScheduleDTO(3, List.of(new DayDTO(2,
                List.of(new FlightDTO("FL45", LocalTime.parse("07:15"), LocalTime.parse("11:09"))))));
        when(restTemplateMock.getForObject(eq(URI.create(URL)), eq(ScheduleDTO.class)))
                .thenReturn(schedule);
    }
}
