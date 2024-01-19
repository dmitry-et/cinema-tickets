package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidAccountException;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

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

        final TicketRequestHelper helper = new TicketRequestHelper().addAll(ticketTypeRequests).check();

        ticketPaymentService.makePayment(accountId, helper.getInfo().getTotalAmountToPay());
        seatReservationService.reserveSeat(accountId, helper.getInfo().getTotalSeatsToAllocate());
    }
}
