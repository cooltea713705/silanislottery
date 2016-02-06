package com.rros.silanislottery;

import com.rros.draw.Drawable;
import com.rros.draw.NoAvailableDrawWithoutReplacementException;

import java.util.*;

/**
 * Class handling a single lottery
 * <p>
 * This class handles the underlying logic for a single lottery.
 *
 * Some exceptions are handled as IllegalStateException: they are not functional.
 */
public class SingleLottery {
    private int pot;

    private Drawable drawableTickets = new Drawable();

    /**
     * Map from ticket to buyer's name
     */
    private Map<Integer, String> ticketBuyerNames = new HashMap<>();

    /**
     * Lottery winners
     */
    private Winner[] winners;

    public SingleLottery(int pot) {
        this.pot = pot;
    }

    /**
     * http://stackoverflow.com/a/6810409/618156
     *
     * @param i input integer
     * @return its ordinal 1 -> 1st, 2 -> 2nd, etc.
     */
    private static String ordinal(final int i) {
        final String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }

    /**
     * Purchase a ticket. This will also update the pot.
     * <p>
     * Given a ticket buyer's first name, this will return a random ticket number that still is available.
     * Ticket number will be returned from 1 to 50.
     * The pot is incremented by TICKET_PRICE.
     * <p>
     * Nice-to-have: similar function where the buyer chooses its ticket number.
     *
     * @param buyerName ticket buyer's first name, has to be not null, not empty, not a white-space only String.
     * @return the ticket number
     * @throws InvalidBuyerNameException  the provided buyer's name is invalid
     * @throws NoAvailableTicketException no more ticket is available for this draw
     */
    public int purchaseTicket(final String buyerName) throws NoAvailableTicketException, InvalidBuyerNameException {
        if (this.isAlreadyDrawn()) {
            throw new SingleLotteryAlreadyDrawnException();
        }

        if (buyerName == null || buyerName.trim().isEmpty()) {
            throw new InvalidBuyerNameException("The buyer's name is expected to be a non-empty String");
        }

        // draw ticket
        final int ticket;
        try {
            ticket = this.drawableTickets.drawWithoutReplacement();
        } catch (NoAvailableDrawWithoutReplacementException e) {
            throw new NoAvailableTicketException();
        }

        if (this.ticketBuyerNames.containsKey(ticket)) {
            // Should not occur due to how Drawable.drawWithoutReplacement() is implemented
            throw new IllegalStateException("Ticket " + ticket + " has already been bought.");
        }

        // Store buyer
        this.ticketBuyerNames.put(ticket, buyerName);

        // update pot
        this.pot += SilanisLottery.TICKET_PRICE;

        return ticket;
    }

    /**
     * Draw lottery.
     *
     * This operation is only possible once.
     * <p>
     * This operation updates the pot: if a winning ball's ticket has been
     * purchased, the winning prize is subtracted to the pot, otherwise the
     * pot is left untouched.
     * This operation closes this lottery:
     * - no more purchase available
     * - winners are defined
     *
     * @return the values of the drawn balls
     */
    public int[] drawLottery() {
        if (this.isAlreadyDrawn()) {
            throw new SingleLotteryAlreadyDrawnException();
        }

        final Drawable drawableBalls = new Drawable();
        final int[] drawResults = new int[SilanisLottery.NB_WINNERS];

        final List<Winner> winnersList = new ArrayList<>();
        final int[] prizes = this.computePrizes();
        for (int i = 0; i < SilanisLottery.NB_WINNERS; i++) {
            // 1- draw
            final int drawResult;
            try {
                drawResult = drawableBalls.drawWithoutReplacement();
            } catch (NoAvailableDrawWithoutReplacementException e) {
                throw new IllegalStateException("Unexpected state occurs if there is not enough balls to draw up to NB_WINNERS", e);
            }

            drawResults[i] = drawResult;

            // 2- add winner
            if (!this.ticketBuyerNames.containsKey(drawResult)) {
                winnersList.add(null);
            } else {
                winnersList.add(
                        // prizes is supposed to have NB_WINNERS elements
                        new Winner(this.ticketBuyerNames.get(drawResult), prizes[i])
                );

                // 3- update pot
                this.pot -= prizes[i];
            }

        }
        this.winners = winnersList.toArray(new Winner[SilanisLottery.NB_WINNERS]);
        return drawResults;

    }

    /**
     * Indicates this has already been drawn
     *
     * @return true if this has already been drawn, false otherwise
     */
    private boolean isAlreadyDrawn() {
        return this.winners != null;
    }

    /**
     * Compute the values of the prizes for the current value of the pot.
     * <p>
     * 75%, 15% and 10% of the pot truncated down to an integer value.
     *
     * @return Array of NB_WINNERS int corresponding to the prizes.
     */
    int[] computePrizes() {
        return new int[]{this.pot / 2 * 3 / 4, this.pot / 2 * 3 / 20, this.pot / 20};
    }

    /**
     * Get the winners of this lottery.
     *
     * @return Array of NB_WINNERS Winner, one or more element might be null
     * if the corresponding winning ball's ticket has not been
     * purchased.
     * @throws SingleLotteryNotDrawnException the lottery has not been drawn
     */
    public Winner[] getWinners() throws SingleLotteryNotDrawnException {
        if (!this.isAlreadyDrawn()) {
            throw new SingleLotteryNotDrawnException();
        }
        return this.winners;
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
     * @throws SingleLotteryNotDrawnException the lottery has not been drawn
     */
    public String generateWinnersMessage() throws SingleLotteryNotDrawnException {
        if (!this.isAlreadyDrawn()) {
            throw new SingleLotteryNotDrawnException();
        }

        final StringJoiner firstLineSj = new StringJoiner("\t");
        final StringJoiner secondLineSj = new StringJoiner("\t");
        final int[] prizes = this.computePrizes();
        for (int i = 0; i < this.winners.length; i++) {
            firstLineSj.add(SingleLottery.ordinal(i) + " ball");
            final String secondLinePart = winners[i] == null ? "No winner" : winners[i].getFirstName();
            secondLineSj.add(secondLinePart + ": " + prizes[i] + "$");
        }
        return String.format("%s%n%s", firstLineSj.toString(), secondLineSj.toString());
    }

    /**
     * @return the current pot value
     */
    public int getPot() {
        return pot;
    }

    /**
     * @return true if a ticket is available for the current draw, false otherwise
     */
    boolean isTicketAvailable() {
        return this.drawableTickets.isDrawWithoutReplacementAvailable();
    }
}
