package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.*;

import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;

public class TicketServiceImpl implements TicketService {

    public final static int MAX_TICKETS = 20;

    private static class PreProcessedRequest {
        private int totalAmountToPay = 0;
        private int totalSeatsToAllocate = 0;
        private int totalTickets = 0;
        private boolean adultTicketPresent = false;

        void add(TicketTypeRequest ticketTypeRequest) {
            if(ticketTypeRequest == null) throw new InvalidRequestException(); //TODO: Consider a new exception class
            int noOfTickets = ticketTypeRequest.getNoOfTickets();
            if(noOfTickets <= 0) throw new InvalidRequestException(); //TODO: Consider a new exception class
            totalTickets += noOfTickets;
            if(totalTickets > MAX_TICKETS) throw new TooManyTicketsException();
            totalSeatsToAllocate += noOfTickets;
            TicketTypeRequest.Type ticketType = ticketTypeRequest.getTicketType();
            adultTicketPresent = adultTicketPresent || (ADULT == ticketType);
            int price = getPricePerTicket(ticketType);
            totalAmountToPay += (price * noOfTickets);
        }
    }

    private static int getPricePerTicket(TicketTypeRequest.Type ticketType) { //TODO: Consider float type for a price
        switch(ticketType) {
            case ADULT: return 20;
            case CHILD: return 10;
            case INFANT: return 0;
            default: throw new UnknownTicketTypeException();
        }
    }

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

        PreProcessedRequest preProcessedRequest = new PreProcessedRequest();
        for(TicketTypeRequest ticketTypeRequest: ticketTypeRequests) preProcessedRequest.add(ticketTypeRequest);
        if(!preProcessedRequest.adultTicketPresent) throw new NoAdultTicketsException();
        ticketPaymentService.makePayment(accountId, preProcessedRequest.totalAmountToPay);
        seatReservationService.reserveSeat(accountId, preProcessedRequest.totalSeatsToAllocate);
    }

    private void verify(Long accountId) throws InvalidAccountException {
        if(accountId == null || accountId <= 0) throw new InvalidAccountException();
    }
}
