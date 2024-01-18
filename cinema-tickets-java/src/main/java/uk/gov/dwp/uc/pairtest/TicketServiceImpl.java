package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.*;

import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;

public class TicketServiceImpl implements TicketService {

    public final static int MAX_REQUESTS = 20;

    private final TicketPaymentService ticketPaymentService;

    private final SeatReservationService seatReservationService;

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

        verify(accountId);

        int totalAmountToPay = 0;
        int totalSeatsToAllocate = 0;
        int totalTickets = 0;
        boolean adult = false;
        for(TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            if(ticketTypeRequest == null) throw new InvalidRequestException(); //TODO: Consider a new exception class
            int noOfTickets = ticketTypeRequest.getNoOfTickets();
            if(noOfTickets <= 0) throw new InvalidRequestException(); //TODO: Consider a new exception class
            totalTickets += noOfTickets;
            if(totalTickets > MAX_REQUESTS) throw new TooManyTicketsException();
            totalSeatsToAllocate += noOfTickets;
            TicketTypeRequest.Type ticketType = ticketTypeRequest.getTicketType();
            adult = adult || (ADULT == ticketType);
            int price = getPricePerTicket(ticketType);
            totalAmountToPay += (price * noOfTickets);
        }
        if(!adult) throw new NoAdultTicketsException();
        ticketPaymentService.makePayment(accountId, totalAmountToPay);
        seatReservationService.reserveSeat(accountId, totalSeatsToAllocate);
    }

    private int getPricePerTicket(TicketTypeRequest.Type ticketType) { //TODO: Consider float type for a price
        switch(ticketType) {
            case ADULT: return 20;
            case CHILD: return 10;
            case INFANT: return 0;
            default: throw new UnknownTicketTypeException();
        }
    }

    private void verify(Long accountId) throws InvalidAccountException {
        if(accountId == null || accountId <= 0) throw new InvalidAccountException();
    }
}
