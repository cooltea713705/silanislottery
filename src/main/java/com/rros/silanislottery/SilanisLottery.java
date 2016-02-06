package com.rros.silanislottery;

/**
 * Silanis Lottery
 */
public class SilanisLottery {

    /**
     * Number of winners
     */
    public final static int NB_WINNERS = 3;

    /**
     * Initial pot value
     */
    public final static int INITIAL_POT = 200;

    /**
     * Lottery ball largest value (smallest expected to be 1)
     */
    public final static int MAX_BALL = 50;

    /**
     * Ticket price
     */
    public final static int TICKET_PRICE = 10;

    /**
     * Current pot
     */
    private int pot;

    /**
     * Purchase a ticket
     * <p>
     * Given a ticket buyer's first name, this will return a random ticket number that still is available.
     * Ticket number will be returned from 1 to 50
     * <p>
     * Nice-to-have: similar function where the buyer chooses its ticket number.
     *
     * @param buyerName ticket buyer's first name
     * @return the ticket number
     * @throws NoAvailableTicketException no more ticket is available for this draw
     */
    public int purchaseTicket(final String buyerName) throws NoAvailableTicketException {
        // not implemented
        throw new RuntimeException("Not implemented");
    }

    /**
     * Draw lottery.
     *
     * Nota: this operation updates the pot: if a winning ball's ticket
     * has been purchased, the winning prize is subtracted to the pot,
     * otherwise the pot is left untouched.
     *
     * @return the values of the drawn balls
     */
    public int[] drawLottery() {
        // not implemented
        throw new RuntimeException("Not implemented");
    }

    /**
     * Compute the values of the prizes for the current value of the pot.
     * <p>
     * 75%, 15% and 10% of the pot rounded to the nearest integer value.
     *
     * @return Array of NB_WINNERS int corresponding to the prizes.
     */
    int[] computePrizes() {
        // not implemented
        throw new RuntimeException("Not implemented");
    }

    /**
     * Get the winners of the latest draw.
     *
     * @return Array of NB_WINNERS Winner, one or more element might be null
     *         if the corresponding winning ball's ticket has not been
     *         purchased.
     * @throws NoPreviousDrawException there was no previous draw
     */
    Winner[] getWinners() throws NoPreviousDrawException {
        // not implemented
        throw new RuntimeException("Not implemented");
    }

    /**
     * Produce the string displaying the winners of the latest draw in standard output
     * <p>
     * The expected format is:
     * 1st ball             2nd ball            3rd ball
     * Dave: 75$            Remy: 15$           Greg: 10$
     * <p>
     * "No winner" is displayed if the ticket corresponding to a ball
     * was not purchased.
     *
     * @return string displaying the winners
     * @throws NoPreviousDrawException "You should draw first" if there was no previous draw
     */
    public String generateWinnersMessage() throws NoPreviousDrawException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return the current pot value
     */
    int getPot() {
        return pot;
    }

    /**
     * @return true if a ticket is available for the current draw, false otherwise
     */
    boolean isTicketAvailable() {
        throw new RuntimeException("Not implemented");
    }
}
