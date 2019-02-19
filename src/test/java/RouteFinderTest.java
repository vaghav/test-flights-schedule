import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import com.ryanair.flights.services.RouteFinder;
import com.ryanair.flights.services.RouteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteFinderTest {

    @Mock
    private RouteService routeServiceMock;

    @InjectMocks
    private RouteFinder routeFinder;

    @Test
    public void shouldPrintAirPortCodes() {
        //given
        when(routeServiceMock.getFlightRoutes()).thenReturn(createRoutes());

        //when
        routeFinder.findRoutes("WRO", "MAD", createRoutes());

        //then
        //TODO: Assertions needs
    }

    private List<RouteDTO> createRoutes() {
        return List.of(new RouteDTO("WRO", "GRO"),
                new RouteDTO("WAW", "WRO"),
                new RouteDTO("GRO", "WAW"), new RouteDTO("WRO", "BCN"),
                new RouteDTO("BCN", "SVO"), new RouteDTO("WRO", "SVO"),
                new RouteDTO("WAW", "SVO"), new RouteDTO("WRO", "LIS"),
                new RouteDTO("WRO", "SVO"), new RouteDTO("SVO", "WAW"),
                new RouteDTO("BCN", "QAT"), new RouteDTO("QAT", "FRN"),
                new RouteDTO("FRN", "CCV"), new RouteDTO("CCV", "SSS"),
//                new RouteDTO("WRO", "AST"), new RouteDTO("AST", "SSS"),
//                new RouteDTO("SSS", "MAD"), new RouteDTO("SSS", "DAV"),
                new RouteDTO("WAW", "MAD"), new RouteDTO("MAD", "BCN"));
    }
}
