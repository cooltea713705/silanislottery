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
     * Previous lottery
     */
    private SingleLottery previousLottery;

    /**
     * Current lottery
     */
    private SingleLottery currentLottery = new SingleLottery(INITIAL_POT);

    /**
     * Purchase a ticket.
     *
     * Delegates to SingleLottery.purchaseTicket() for the current lottery.
     *
     * @param buyerName ticket buyer's first name, has to be not null, not empty, not a white-space only String.
     * @return the ticket number
     * @throws InvalidBuyerNameException the provided buyer's name is invalid
     * @throws NoAvailableTicketException no more ticket is available for this draw
     */
    public int purchaseTicket(final String buyerName) throws NoAvailableTicketException, InvalidBuyerNameException {
        return this.currentLottery.purchaseTicket(buyerName);
    }

    /**
     * Draw lottery.
     *
     * Delegates to SingleLottery.drawLottery() for the current lottery and
     * then saves the previous lottery and start anew the current one.
     *
     * @return the values of the drawn balls
     */
    public int[] drawLottery() {
        final int[] lotteryResults = this.currentLottery.drawLottery();
        this.previousLottery = this.currentLottery;
        this.currentLottery = new SingleLottery(this.previousLottery.getPot());
        return lotteryResults;
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
        if (this.previousLottery == null) {
            throw new NoPreviousDrawException();
        }

        try {
            return this.previousLottery.getWinners();
        } catch (final SingleLotteryNotDrawnException e) {
            throw new IllegalStateException("The previous lottery should have been drawn", e);
        }
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
        return this.currentLottery.getPot();
    }

    /**
     * @return true if a ticket is available for the current draw, false otherwise
     */
    boolean isTicketAvailable() {
        throw new RuntimeException("Not implemented");
    }
}
