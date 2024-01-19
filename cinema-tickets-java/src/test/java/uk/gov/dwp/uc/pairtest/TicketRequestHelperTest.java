package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidRequestException;

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
}
