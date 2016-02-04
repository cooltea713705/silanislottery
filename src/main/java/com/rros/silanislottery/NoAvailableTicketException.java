package com.rros.silanislottery;

/**
 * No ticket is available
 */
public class NoAvailableTicketException extends Exception {

    public NoAvailableTicketException() {
        super("There is no more available ticket for the current draw.");
    }
}
