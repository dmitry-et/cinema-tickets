package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.NoAdultTicketsException;
import uk.gov.dwp.uc.pairtest.exception.TooManyInfantsPerAdultException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
@RunWith(MockitoJUnitRunner.class)
public class TicketRequestHelperTest {

    @Test
    public void checkNoOfTicketsAndSeatReservations() {
        final TicketTypeRequest[] requests = new TicketTypeRequest[]{
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 7)
        };
        TicketRequestHelper helper = new TicketRequestHelper().addAll(requests).completeChecks();
        assertEquals(3 * 0 + 2 * 10 + 7 * 20, helper.getTotalAmountToPay());
        assertEquals(2 + 7, helper.getTotalSeatsToAllocate());
    }
}
