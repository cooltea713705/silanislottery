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
    private SingleLottery currentLottery;

    /**
     * Default behaviour: pot is INITIAL_POT
     */
    public SilanisLottery() {
        this(INITIAL_POT);
    }

    /**
     * This constructor is used so that we can handle a parameterized pot
     *
     * @param pot input pot
     */
    public SilanisLottery(final int pot) {
        this(new SingleLottery(pot));
    }

    /**
     * This constructor is used for test purposes.
     *
     * @param currentLottery input current lottery
     */
    SilanisLottery(final SingleLottery currentLottery) {
        this.currentLottery = currentLottery;
    }

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
     * Produce the string displaying the winners of the latest draw
     *
     * Delegates to SingleLottery.generateWinnersMessage() for the
     * previous lottery (if there was one).
     *
     * @return string displaying the winners of the latest draw
     * @throws NoPreviousDrawException "You should draw first" if there was no previous draw
     */
    public String generateWinnersMessage() throws NoPreviousDrawException {
        if (this.previousLottery == null) {
            throw new NoPreviousDrawException();
        }

        return this.previousLottery.generateWinnersMessage();
    }

    /**
     * Used for test purposes
     * @return the previous lottery
     */
    SingleLottery getPreviousLottery() {
        return previousLottery;
    }

    /**
     * Used for test purposes
     * @return true if tickets are available for the current lottery, false otherwise
     */
    boolean isTicketAvailable() {
        return this.currentLottery.isTicketAvailable();
    }

    /**
     * Used for test purposes
     * @return the previous lottery winners
     */
    Winner[] getWinners() {
        return this.previousLottery.getWinners();
    }

    /**
     * @return the current pot value
     */
    public int getPot() {
        return this.currentLottery.getPot();
    }

}
