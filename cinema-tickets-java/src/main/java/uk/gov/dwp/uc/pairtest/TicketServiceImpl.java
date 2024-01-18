package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.*;

import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;

public class TicketServiceImpl implements TicketService {

    private final TicketPaymentService ticketPaymentService;

    private final SeatReservationService seatReservationService;

    private static void verifyAccount(Long accountId) throws InvalidAccountException {
        if(accountId == null || accountId <= 0) throw new InvalidAccountException();
    }

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        super();
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        verifyAccount(accountId);

        TicketRequestHelper helper = new TicketRequestHelper().addAll(ticketTypeRequests).completeChecks();

        ticketPaymentService.makePayment(accountId, helper.getTotalAmountToPay());
        seatReservationService.reserveSeat(accountId, helper.getTotalSeatsToAllocate());
    }
}
