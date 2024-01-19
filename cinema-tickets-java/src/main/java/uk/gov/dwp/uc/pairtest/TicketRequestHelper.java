package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.*;

class TicketRequestHelper {
    public static int MAX_TICKETS = 20;

    static class TicketRequestInfo {
        private int totalAmountToPay = 0;
        private int totalSeatsToAllocate = 0;
        private int totalTickets = 0;
        private int adultTickets = 0;
        private int infantTickets = 0;

        public int getTotalAmountToPay() {
            return totalAmountToPay;
        }

        public int getTotalSeatsToAllocate() {
            return totalSeatsToAllocate;
        }

        private void addTickets(final TicketTypeRequest.Type ticketType, final int noOfTickets) {
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

    private final TicketRequestInfo info = new TicketRequestInfo();
    private static int getPricePerTicket(final TicketTypeRequest.Type ticketType) { //TODO: Consider float type for a price
        switch(ticketType) {
            case ADULT: return 20;
            case CHILD: return 10;
            case INFANT: return 0;
            default: throw new UnknownTicketTypeException();
        }
    }

    public TicketRequestInfo getInfo() {
        return info;
    }

    TicketRequestHelper add(final TicketTypeRequest ticketTypeRequest) throws InvalidRequestException {
        if(ticketTypeRequest == null) throw new NullRequestException();
        int noOfTickets = ticketTypeRequest.getNoOfTickets();
        if(noOfTickets <= 0) throw new ZeroOrNegativeTicketNoException();
        info.totalTickets += noOfTickets;
        if(info.totalTickets > MAX_TICKETS) throw new TooManyTicketsException();
        TicketTypeRequest.Type ticketType = ticketTypeRequest.getTicketType();
        info.addTickets(ticketType, noOfTickets);
        info.totalAmountToPay += (getPricePerTicket(ticketType) * noOfTickets);
        return this;
    }

    TicketRequestHelper addAll(final TicketTypeRequest... ticketTypeRequests) throws InvalidRequestException {
        for(TicketTypeRequest ticketTypeRequest: ticketTypeRequests) add(ticketTypeRequest);
        return this;
    }

    TicketRequestHelper check() throws InvalidRequestException {
        if(info.adultTickets < 1) throw new ZeroAdultTicketNoException();
        if(info.infantTickets > info.adultTickets) throw new TooManyInfantsPerAdultException();
        return this;
    }
}