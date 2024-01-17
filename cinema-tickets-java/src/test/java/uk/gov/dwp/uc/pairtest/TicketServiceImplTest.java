package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private TicketPaymentService ticketPaymentService;

    @Mock
    private SeatReservationService seatReservationService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void validateRequestLimit() {
        Long accountId = 0L;
        try {
            TicketTypeRequest[] requests = new TicketTypeRequest[TicketServiceImpl.MAX_REQUESTS + 1];
            ticketService.purchaseTickets(accountId, requests);
            fail(String.format(
                    "InvalidPurchaseException is not thrown when request limit %d is exceeded",
                    TicketServiceImpl.MAX_REQUESTS));
        } catch (InvalidPurchaseException e) {
            assertTrue(true);
        }
    }

    @Test
    public void validate() {
        assert(true);
    }
}
