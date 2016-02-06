package com.rros.silanislottery;

import com.rros.draw.Drawable;
import com.rros.draw.NoAvailableDrawWithoutReplacementException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class handling a single lottery
 * <p>
 * This class handles the underlying logic for a single lottery.
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
        final Drawable drawableBalls = new Drawable();
        final int[] drawResults = new int[SilanisLottery.NB_WINNERS];

        try {
            final List<Winner> winnersList = new ArrayList<>();
            final int[] prizes = this.computePrizes();
            for (int i = 0; i < SilanisLottery.NB_WINNERS; i++) {
                final int drawResult = drawableBalls.drawWithoutReplacement();
                drawResults[i] = drawResult;

                if (!this.ticketBuyerNames.containsKey(drawResult)) {
                    winnersList.add(null);
                } else {
                    winnersList.add(
                            // prizes is supposed to have NB_WINNERS elements
                            new Winner(this.ticketBuyerNames.get(drawResult), prizes[i])
                    );
                }
            }
            this.winners = winnersList.toArray(new Winner[SilanisLottery.NB_WINNERS]);
            return drawResults;
        } catch (NoAvailableDrawWithoutReplacementException e) {
            throw new IllegalStateException("Unexpected state: there is not enough balls to draw up to NB_WINNERS", e);
        }
    }

    /**
     * Compute the values of the prizes for the current value of the pot.
     * <p>
     * 75%, 15% and 10% of the pot rounded to the nearest integer value.
     *
     * @return Array of NB_WINNERS int corresponding to the prizes.
     */
    int[] computePrizes() {
        return new int[]{this.pot * 3 / 4, this.pot * 3 / 20, this.pot / 10};
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
        if (this.winners == null) {
            throw new SingleLotteryNotDrawnException("This lottery has not been drawn yet");
        }
        return this.winners;
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
        throw new RuntimeException("Not implemented");
    }

}
