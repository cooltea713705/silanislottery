package com.rros.silanislottery;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for com.rros.silanislottery.SilanisLottery
 */
public class SilanisLotteryTest {

    public static final String TEST_BUYER_NAME = "TEST_BUYER_NAME";
    public static final Logger LOGGER = Logger.getGlobal();
    // This pattern matches: n-th ball
    private final static Pattern FIRST_LINE_PATTERN = Pattern.compile("[0-9]+[a-z]+ ball");
    // This pattern matches: Dave: 75$
    private final static Pattern SECOND_LINE_PATTERN = Pattern.compile("\\s*[^:]+: [0-9]+\\$");
    private SilanisLottery lottery;

    @Before
    public void setUp() throws Exception {
        this.lottery = new SilanisLottery();
    }

    @Test
    public void testPurchaseTicket() throws Exception {
        final int initialPot = this.lottery.getPot();
        assertThat(initialPot).isEqualTo(SilanisLottery.INITIAL_POT);
        assertThat(this.lottery.purchaseTicket(TEST_BUYER_NAME)).isBetween(1, SilanisLottery.MAX_BALL);

        final int newPot = this.lottery.getPot();
        assertThat(newPot).as("Pot value was updated consecutively to ticket purchase").isEqualTo(initialPot + SilanisLottery.TICKET_PRICE);
    }

    // TODO test distribution of balls drawn

    @Test
    public void testPurchaseTicketThrowsNoMoreTicketException() throws Exception {
        final List<Integer> boughtTickets = new ArrayList<>();
        try {
            while (this.lottery.isTicketAvailable()) {
                boughtTickets.add(this.lottery.purchaseTicket(TEST_BUYER_NAME));
            }
        } catch (final NoAvailableTicketException e) {
            fail("Unexpected exception", e);
        }
        assertThat(boughtTickets).hasSize(SilanisLottery.MAX_BALL);
        // We expect the SilanisLottery.MAX_BALL + 1-th draw (and all subsequent) throws exception
        assertThatExceptionOfType(NoAvailableTicketException.class).isThrownBy(() -> this.lottery.purchaseTicket(TEST_BUYER_NAME));
        assertThatExceptionOfType(NoAvailableTicketException.class).isThrownBy(() -> this.lottery.purchaseTicket(TEST_BUYER_NAME));
    }

    // This pattern matches: %1st drawn value%    %2nd drawn value%   %3rd drawn value%
    // private final static Pattern SECOND_LINE_PATTERN = Pattern.compile("(\\s*[0-9]+\\s*){" + SilanisLottery.NB_WINNERS + "}");

    @Test
    public void testDrawLottery() throws Exception {
        final int[] currentDraw = this.lottery.drawLottery();
        assertThat(currentDraw)
                .hasSize(SilanisLottery.NB_WINNERS);
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
                .as("Previous winning tickets are available for purchase again").contains(Arrays.stream(currentDraw).boxed().toArray(Integer[]::new));
    }


    @Test
    public void testComputePrices() throws Exception {
        // test initialisation
        final int initialPot = this.lottery.getPot();

        // test body
        final int[] prices = this.lottery.computePrices();
        assertThat(prices)
                .hasSize(SilanisLottery.NB_WINNERS);
        assertThat(prices)
                .as("75%, 15% and 10% of half the initial pot")
                .isEqualTo(new int[]{initialPot / 2 * 3 / 4, initialPot / 2 * 3 / 20, initialPot / 2 / 10});
    }

    @Test
    public void testDrawLotteryNoWinnerImpliesPotNotUpdated() throws Exception {
        // test initialisation
        final int initialPot = this.lottery.getPot();

        // test body
        this.lottery.drawLottery();
        assertThat(this.lottery.getPot())
                .as("No winners implies the pot is not updated").isEqualTo(initialPot);
    }

    @Test
    public void testDrawLotteryPotUpdated() throws Exception {
        // Test initialization
        int currentPot = this.lottery.getPot();
        final int nbConsecutiveLotteries = 10;
        final int nbParticipants = 20;

        for (int i = 0; i < nbConsecutiveLotteries; i++) {
            final Set<Integer> tickets = new HashSet<>();
            for (int j = 0; j < nbParticipants; j++) {
                tickets.add(this.lottery.purchaseTicket(TEST_BUYER_NAME));
                // Increase the current pot with ticket price
                currentPot += SilanisLottery.TICKET_PRICE;
            }

            this.lottery.drawLottery();

            // pay the winners:
            for (final Winner winner : this.lottery.getWinners()) {
                if (winner == null) {
                    // ticket was not purchased
                    continue;
                }

                currentPot -= winner.getPrice();
            }
        }

        assertThat(currentPot)
                .as("The pot has been updated correctly")
                .isEqualTo(this.lottery.getPot());
    }

    @Test
    public void testGetWinnersWithNoTicketPurchased() throws Exception {
        // test initialisation
        this.lottery.drawLottery();

        // test body
        final Winner[] winners = this.lottery.getWinners();
        assertThat(winners)
                .hasSize(SilanisLottery.NB_WINNERS)
                .as("No tickets were purchased: we expect null-valued array").isEqualTo(new Winner[SilanisLottery.NB_WINNERS]);
    }

    @Test
    public void testGetWinnersThrowsNoPreviousDrawException() throws Exception {
        assertThatExceptionOfType(NoPreviousDrawException.class)
                .isThrownBy(() -> this.lottery.getWinners());
    }

    /**
     * We force the lottery draw and observe the output is correct.
     */
    @Test
    public void testGetWinners() throws Exception {
        // TODO
    }

    /**
     * This only tests the String produced is conform. The underlying
     * logic is tested elsewhere.
     */
    @Test
    public void testProduceDisplayWinners() throws Exception {
        // test initialisation
        this.lottery.drawLottery();

        // test body
        final String result = this.lottery.produceDisplayWinners();
        assertThat(result).isNotEmpty();
        final String[] splitResult = result.split("\\r?\\n");
        assertThat(splitResult).hasSize(2);

        {
            // assertions on first line
            final String[] splitLine = splitResult[0].trim().split("\\t");
            assertThat(splitLine).hasSize(SilanisLottery.NB_WINNERS);
            for (final String winner : splitLine) {
                assertThat(winner).containsPattern(FIRST_LINE_PATTERN);
            }
        }

        {
            // assertions on second line
            final String[] splitLine = splitResult[1].trim().split("\\t");
            assertThat(splitLine).hasSize(SilanisLottery.NB_WINNERS);
            for (final String winner : splitLine) {
                assertThat(winner).containsPattern(SECOND_LINE_PATTERN);
            }
        }
    }

    @Test
    public void testProduceDisplayWinnersThrowsNoPreviousDrawException() throws Exception {
        assertThatExceptionOfType(NoPreviousDrawException.class).isThrownBy(
                () -> this.lottery.produceDisplayWinners()
        );
        this.lottery.drawLottery();

        try {
            final String msg = this.lottery.produceDisplayWinners();
            LOGGER.info("Result:\n" + msg);
        } catch (final NoPreviousDrawException e) {
            fail("Unexpected exception: the lottery has been drawn");
        }
    }

}
