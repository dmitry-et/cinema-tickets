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
import uk.gov.dwp.uc.pairtest.exception.*;

import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.dwp.uc.pairtest.TicketRequestHelper.MAX_TICKETS;

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
    public void nullAccount() throws InvalidPurchaseException {
        try {
            ticketService.purchaseTickets(null);
            fail("InvalidAccountException is not thrown for the null account id");
        } catch (InvalidAccountException ignored) {
        }
    }

    @Test
    public void invalidAccount() throws InvalidPurchaseException {
        final Long invalidAccountId = 0L;
        try {
            ticketService.purchaseTickets(invalidAccountId);
            fail(String.format(
                    "InvalidAccountException is not thrown for the account id %d which is not greater than 0",
                    invalidAccountId));
        } catch (InvalidAccountException ignored) {
        }
    }

    @Test
    public void invalidRequestLimit() throws InvalidPurchaseException {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[MAX_TICKETS + 1];
        for(int i = 0; i < requests.length; i++) {
            requests[i] = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        }
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail(String.format(
                    "InvalidPurchaseException is not thrown when request limit %d is exceeded",
                    MAX_TICKETS));
        } catch (TooManyTicketsException ignored) {
        }
    }

    @Test
    public void invalidRequestLimit0() throws InvalidPurchaseException {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, MAX_TICKETS + 1)
        };
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail(String.format(
                    "InvalidPurchaseException is not thrown when request limit %d is exceeded",
                    MAX_TICKETS));
        } catch (TooManyTicketsException ignored) {
        }
    }

    @Test
    public void nullRequest() throws InvalidPurchaseException {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{ null };
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail("NullRequestException is not thrown when request is null");
        } catch (NullRequestException ignored) {
        }
    }

    @Test
    public void noOfTicketsIsNotPositive() throws InvalidPurchaseException {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0)
        };
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail("ZeroOrNegativeTicketNoException is not thrown when no of ticket in the request is 0");
        } catch (ZeroOrNegativeTicketNoException ignored) {
        }
    }

    @Test
    public void noAdultTickets() throws InvalidPurchaseException {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1),
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
        };
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail("NoAdultTicketsException is not thrown when no adult tickets are requested");
        } catch (ZeroAdultTicketNoException ignored) {
        }
    }

    @Test
    public void checkNoOfInfantsAndAdults() throws InvalidPurchaseException {
        final Long accountId = 1L;
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
        };
        try {
            ticketService.purchaseTickets(accountId, requests);
            fail("TooManyInfantsPerAdultException is not thrown when the number of infants is greater than the number og adults");
        } catch (TooManyInfantsPerAdultException ignored) {
        }
    }
}
