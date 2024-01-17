package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    @Test
    public void validateRequestLimit() {
        TicketService ticketService = new TicketServiceImpl();
        Long accountId = Long.valueOf(0);
        try {
            TicketTypeRequest[] requests = new TicketTypeRequest[21];
            ticketService.purchaseTickets(accountId, requests);
            assertTrue("InvalidPurchaseException is not thrown when request limit is exceeded", false);
        } catch (InvalidPurchaseException e) {
            assertTrue(true);
        }
    }

    @Test
    public void validate() {
        assert(true);
    }
}
