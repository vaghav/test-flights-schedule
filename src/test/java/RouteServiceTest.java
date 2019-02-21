import com.ryanair.flights.enums.Operator;
import com.ryanair.flights.internal.downstream.dto.RouteDTO;
import com.ryanair.flights.services.RouteService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
    private RouteService routeService;

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
}
