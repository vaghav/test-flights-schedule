import com.ryanair.flights.services.dto.ItineraryDTO;
import com.ryanair.flights.services.dto.LegDTO;
import com.ryanair.flights.services.FlightsSearchServiceImpl;
import com.ryanair.flights.route.api.RouteServiceImpl;
import com.ryanair.flights.schedule.api.SchedulesServiceImpl;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(JUnitParamsRunner.class)
public class CommonFlightSearcherServiceTest {

    protected static final LocalDateTime departureDateTime = LocalDateTime.parse("2019-03-02T07:00");
    protected static final LocalDateTime arrivalDateTime = LocalDateTime.parse("2019-03-02T22:10");

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    protected RouteServiceImpl routeServiceMock;

    @Mock
    protected SchedulesServiceImpl schedulesServiceMock;

    @InjectMocks
    protected FlightsSearchServiceImpl searcherService;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(searcherService, "flightsIntervalInHours", 2, Integer.class);
    }

    protected ItineraryDTO createItinerary(LegDTO... legs) {
        return new ItineraryDTO(List.of(legs));
    }
}
