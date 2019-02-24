import com.ryanair.flights.route.api.dto.RouteDTO;
import com.ryanair.flights.enums.Operator;
import com.ryanair.flights.route.api.RouteServiceImpl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteServiceTest {

    private static final String URL = "https://services-api.ryanair.com/locate/3/routes";

    @Mock
    private RestTemplate restTemplateMock;

    @InjectMocks
    private RouteServiceImpl routeService;

    @Test
    public void shouldReturnRoutes() {
        //given
        RouteDTO firstRoute = new RouteDTO();
        firstRoute.setConnectingAirport("WRO");
        firstRoute.setOperator(Operator.RYANAIR.name());

        RouteDTO secondRoute = new RouteDTO();
        secondRoute.setConnectingAirport(null);
        secondRoute.setOperator(Operator.RYANAIR.name());

        when(restTemplateMock.getForObject(URI.create(URL), RouteDTO[].class))
                .thenReturn(new RouteDTO[]{firstRoute, secondRoute});

        //when
        List<RouteDTO> flightRoutes = routeService.getFlightRoutes();

        //then
        Assert.assertEquals(1, flightRoutes.size());
        Assert.assertSame(secondRoute, flightRoutes.get(0));
    }

    @Ignore
    @Test
    public void shouldPrintAirPortCodes() {

        //when
        routeService.findPaths("WRO", "MAD", createRoutes(), 7);

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
                new RouteDTO("WRO", "AST"), new RouteDTO("AST", "SSS"),
                new RouteDTO("SSS", "MAD"), new RouteDTO("SSS", "DAV"),
                new RouteDTO("WAW", "MAD"), new RouteDTO("MAD", "BCN"));
    }
}
