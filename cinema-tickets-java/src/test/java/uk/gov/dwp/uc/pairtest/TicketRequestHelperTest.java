package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidRequestException;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static uk.gov.dwp.uc.pairtest.TicketRequestHelper.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketRequestHelperTest {

    @Test
    public void checkNoOfTicketsAndSeatReservations() throws InvalidRequestException {
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5)
        };
        TicketRequestHelper helper = new TicketRequestHelper().addAll(requests).check();
        assertEquals(3 * INFANT_TICKET_PRICE + 2 * CHILD_TICKET_PRICE + 7 * ADULT_TICKET_PRICE,
                helper.getInfo().getTotalAmountToPay());
        assertEquals(2 + 7, helper.getInfo().getTotalSeatsToAllocate());
    }

    @Test
    public void randomizedCheckNoOfTicketsAndSeatReservations() throws InvalidRequestException {
        final Random rnd = ThreadLocalRandom.current();
        final int infants = rnd.nextInt(1, 16);
        final int children = rnd.nextInt(1, 16);
        final int adults = rnd.nextInt(infants, 32);
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, adults),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, infants),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, children)
        };
        TicketRequestHelper helper = new TicketRequestHelper().addAll(requests).check();
        assertEquals(infants * INFANT_TICKET_PRICE + children * CHILD_TICKET_PRICE + adults * ADULT_TICKET_PRICE,
                helper.getInfo().getTotalAmountToPay());
        assertEquals(children + adults, helper.getInfo().getTotalSeatsToAllocate());
    }
}
