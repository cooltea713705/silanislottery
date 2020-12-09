package com.rros.silanislottery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

/**
 * Test class for com.rros.silanislottery.SilanisLottery
 */
public class SilanisLotteryTest {

    public static final String TEST_BUYER_NAME = "TEST_BUYER_NAME";

    private SilanisLottery lottery;

    private SingleLottery mockCurrentSingleLottery;

    @BeforeEach
    public void setUp() throws Exception {
        this.mockCurrentSingleLottery = mock(SingleLottery.class);
        this.lottery = new SilanisLottery(this.mockCurrentSingleLottery);
    }

    /**
     * Test purchaseTicket() is delegated to SingleLottery
     */
    @Test
    public void testPurchaseTicket() throws Exception {
        this.lottery.purchaseTicket(TEST_BUYER_NAME);
        // expect strictly one call to SingleLottery.purchaseTicket(String)
        verify(this.mockCurrentSingleLottery, times(1)).purchaseTicket(TEST_BUYER_NAME);
    }

    /** Test drawLottery() */
    @Test
    public void testDrawLottery() throws Exception {
        assertThat(this.lottery.getPreviousLottery()).isNull();

        this.lottery.drawLottery();
        // expect strictly one call delegated to SingleLottery.drawLottery
        verify(this.mockCurrentSingleLottery, times(1)).drawLottery();
        assertThat(this.lottery.getPreviousLottery())
                .isNotNull()
                .as("The 'current lottery' should have been assigned to SilanisLottery.previousLottery")
                .isEqualTo(this.mockCurrentSingleLottery);
    }

    /**
     * Test drawLottery() twice
     */
    @Test
    public void testDrawLotteryTwice() throws Exception {
        assertThat(this.lottery.getPreviousLottery()).isNull();

        this.lottery.drawLottery();
        verify(this.mockCurrentSingleLottery, times(1)).drawLottery();

        reset(this.mockCurrentSingleLottery);
        this.lottery.drawLottery();
        // expect this.mockCurrentSingleLottery.drawLottery() to not be called once again
        verify(this.mockCurrentSingleLottery, never()).drawLottery();
        assertThat(this.lottery.getPreviousLottery())
                .isNotNull()
                .as("The mock is not assigned to SilanisLottery.previousLottery anymore")
                .isNotEqualTo(this.mockCurrentSingleLottery);
    }

    /**
     * Test the pot is updated correctly after multiple lotteries
     */
    @Test
    public void testDrawLotteryPotUpdated() throws Exception {
        // Test initialisation
        // Does not use mock
        this.lottery = new SilanisLottery();
        int currentPot = this.lottery.getPot();
        final int nbConsecutiveLotteries = 10;
        final int nbParticipants = 20;

        // Test body
        for (int i = 0; i < nbConsecutiveLotteries; i++) {
            for (int j = 0; j < nbParticipants; j++) {
                this.lottery.purchaseTicket(TEST_BUYER_NAME);
                // Increase the current pot with ticket price
                currentPot += SilanisLottery.TICKET_PRICE;
            }

            this.lottery.drawLottery();

            // pay the winners: pot has to be updated by subtracting their prizes
            for (final Winner winner : this.lottery.getWinners()) {
                if (winner == null) {
                    // ticket was not purchased
                    continue;
                }

                currentPot -= winner.getPrize();
            }
        }
        assertThat(this.lottery.getPot())
                .as("The pot value")
                .isEqualTo(currentPot);
    }

    /** Test purchaseTicket() after drawLottery(): the previous winning tickets should be available for purchase again */
    @Test
    public void testPurchasingTicketsDrawLottery() throws Exception {
        // Test initialisation: does not use the mock
        this.lottery = new SilanisLottery();

        // Test body
        final int[] currentDraw = this.lottery.drawLottery();

        for (final int ball : currentDraw) {
            assertThat(ball).isBetween(1, SilanisLottery.MAX_BALL);
        }

        final List<Integer> ticketsAvailableAfterDraw = new ArrayList<>();
        while (this.lottery.isTicketAvailable()) {
            ticketsAvailableAfterDraw.add(this.lottery.purchaseTicket(TEST_BUYER_NAME));
        }
        assertThat(ticketsAvailableAfterDraw)
                .hasSize(SilanisLottery.MAX_BALL)
                // converts int[] to Integer[] http://stackoverflow.com/a/27043087/618156
                .as("All tickets available")
                .contains(Arrays.stream(currentDraw).boxed().toArray(Integer[]::new));
    }

    /**
     * Test generateWinnersMessage()
     */
    @Test
    public void testGenerateWinnersMessage() throws Exception {
        // Test initialisation
        /* upon this method call, this.lottery.previousLottery
         * is assigned with this.lottery.currentLottery which is
         * this.mockCurrentSingleLottery, see testDrawLottery()
         */
        this.lottery.drawLottery();

        // Test body
        this.lottery.generateWinnersMessage();

        // expect strictly one call delegated to SingleLottery.generateWinnersMessage()
        verify(this.mockCurrentSingleLottery, times(1)).generateWinnersMessage();
    }

    /**
     * Test generateWinnersMessage() throws NoPreviousDrawException
     */
    @Test
    public void testGenerateWinnersMessageThrowsNoPreviousDrawException() throws Exception {
        assertThatExceptionOfType(NoPreviousDrawException.class)
                .isThrownBy(() -> this.lottery.generateWinnersMessage());
    }

}
