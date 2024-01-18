package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.*;

class TicketRequestHelper {
    public static int MAX_TICKETS = 20;
    private int totalAmountToPay = 0;
    private int totalSeatsToAllocate = 0;
    private int totalTickets = 0;
    private int adultTickets = 0;
    private int infantTickets = 0;
    private static int getPricePerTicket(TicketTypeRequest.Type ticketType) { //TODO: Consider float type for a price
        switch(ticketType) {
            case ADULT: return 20;
            case CHILD: return 10;
            case INFANT: return 0;
            default: throw new UnknownTicketTypeException();
        }
    }

    public int getTotalAmountToPay() {
        return totalAmountToPay;
    }

    public int getTotalSeatsToAllocate() {
        return totalSeatsToAllocate;
    }

    TicketRequestHelper add(TicketTypeRequest ticketTypeRequest) throws InvalidRequestException {
        if(ticketTypeRequest == null) throw new NullRequestException();
        int noOfTickets = ticketTypeRequest.getNoOfTickets();
        if(noOfTickets <= 0) throw new ZeroOrNegativeTicketNoException();
        totalTickets += noOfTickets;
        if(totalTickets > MAX_TICKETS) throw new TooManyTicketsException();
        TicketTypeRequest.Type ticketType = ticketTypeRequest.getTicketType();
        addTickets(ticketType, noOfTickets);
        totalAmountToPay += (getPricePerTicket(ticketType) * noOfTickets);
        return this;
    }

    TicketRequestHelper addAll(TicketTypeRequest... ticketTypeRequests) throws InvalidRequestException {
        for(TicketTypeRequest ticketTypeRequest: ticketTypeRequests) add(ticketTypeRequest);
        return this;
    }

    TicketRequestHelper completeChecks() throws InvalidRequestException {
        if(adultTickets < 1) throw new NoAdultTicketsException();
        if(infantTickets > adultTickets) throw new TooManyInfantsPerAdultException();
        return this;
    }

    private void addTickets(TicketTypeRequest.Type ticketType, int noOfTickets) {
        switch(ticketType) {
            case ADULT:
                adultTickets += noOfTickets;
                totalSeatsToAllocate += noOfTickets;
                break;
            case INFANT:
                infantTickets += noOfTickets;
                break;
            case CHILD:
                totalSeatsToAllocate += noOfTickets;
                break;
            default: throw new UnknownTicketTypeException();
        }
    }
}