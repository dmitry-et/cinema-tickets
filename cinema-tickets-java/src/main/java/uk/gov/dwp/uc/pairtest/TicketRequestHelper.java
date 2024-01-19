package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.*;

import java.io.*;
import java.net.URL;
import java.util.Properties;

class TicketRequestHelper {
    public static final int MAX_TICKETS;
    public static final int INFANT_TICKET_PRICE;
    public static final int CHILD_TICKET_PRICE;
    public static final int ADULT_TICKET_PRICE;
    static {
        final Properties props = new Properties();
        final URL url = Thread.currentThread().getContextClassLoader().
                getResource("application.properties");
        if(url != null) {
            final String path = url.getPath();
            try (final FileInputStream is = new FileInputStream(path);) {
                props.load(is);
            } catch (IOException ignored) {
            }
        }
        final String sMaxTickets = props.getProperty("ticket.max", "20");
        final String sInfantTicketPrice = props.getProperty("ticket.price.infant", "0");
        final String sChildTicketPrice = props.getProperty("ticket.price.child", "10");
        final String sAdultTicketPrice = props.getProperty("ticket.price.adult", "20");
        int value;
        try {
           value = Integer.parseInt(sMaxTickets);
        } catch(NumberFormatException ignored) {
           value = 20;
        }
        MAX_TICKETS = (value > 0) ? value : 20;
        try {
           value = Integer.parseInt(sInfantTicketPrice);
        } catch(NumberFormatException ignored) {
           value = 0;
        }
        INFANT_TICKET_PRICE = (value >= 0) ? value : 0;
        try {
           value = Integer.parseInt(sChildTicketPrice);
        } catch(NumberFormatException ignored) {
           value = 10;
        }
        CHILD_TICKET_PRICE = (value >= 0) ? value : 10;
        try {
           value = Integer.parseInt(sAdultTicketPrice);
        } catch(NumberFormatException ignored) {
           value = 0;
        }
        ADULT_TICKET_PRICE = (value >= 0) ? value : 20;
    }
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
            case ADULT: return ADULT_TICKET_PRICE;
            case CHILD: return CHILD_TICKET_PRICE;
            case INFANT: return INFANT_TICKET_PRICE;
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