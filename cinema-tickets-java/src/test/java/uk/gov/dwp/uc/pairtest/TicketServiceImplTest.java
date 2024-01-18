package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidAccountException;
import uk.gov.dwp.uc.pairtest.exception.InvalidRequestException;
import uk.gov.dwp.uc.pairtest.exception.TooManyTicketsException;

import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.openMocks;

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
        openMocks(this);
    }

    @Test
    public void nullAccount() {
        final Long invalidAccountId = null;
        try {
            ticketService.purchaseTickets(invalidAccountId);
            fail(String.format(
                    "InvalidAccountException is not thrown for the account id %d is not greater",
                    invalidAccountId));
        } catch (InvalidAccountException ignored) {
        }
    }

    @Test
    public void invalidAccount() {
        final Long invalidAccountId = 0L;
        try {
            ticketService.purchaseTickets(invalidAccountId);
            fail(String.format(
                    "InvalidAccountException is not thrown for the account id %d is not greater",
                    invalidAccountId));
        } catch (InvalidAccountException ignored) {
        }
    }

    @Test
    public void invalidRequestLimit() {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[TicketServiceImpl.MAX_REQUESTS + 1];
        for(int i = 0; i < requests.length; i++) {
            requests[i] = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        }
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail(String.format(
                    "InvalidPurchaseException is not thrown when request limit %d is exceeded",
                    TicketServiceImpl.MAX_REQUESTS));
        } catch (TooManyTicketsException ignored) {
        }
    }

    @Test
    public void invalidRequestLimit0() {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, TicketServiceImpl.MAX_REQUESTS + 1)
        };
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail(String.format(
                    "InvalidPurchaseException is not thrown when request limit %d is exceeded",
                    TicketServiceImpl.MAX_REQUESTS));
        } catch (TooManyTicketsException ignored) {
        }
    }

    @Test
    public void nullRequest() {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{ null };
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail("InvalidRequestException is not thrown when request is null");
        } catch (InvalidRequestException ignored) {
        }
    }

    @Test
    public void noOfTicketsIsNotPositive() {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0)
        };
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail("InvalidRequestException is not thrown when no of ticket in the request is 0");
        } catch (InvalidRequestException ignored) {
        }
    }

}
