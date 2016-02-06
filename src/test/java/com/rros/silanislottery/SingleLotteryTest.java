package com.rros.silanislottery;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for SingleLottery
 */
public class SingleLotteryTest {

    public static final Logger LOGGER = Logger.getGlobal();

    public static final String TEST_BUYER_NAME = "TEST_BUYER_NAME";

    public static final String NEW_LINE_PATTERN = "\\r?\\n";

    // This pattern matches: n-th ball
    public final static Pattern FIRST_LINE_PATTERN = Pattern.compile("[0-9]+[a-z]+ ball");
    // This pattern matches: Dave: 75$
    public final static Pattern SECOND_LINE_PATTERN = Pattern.compile("\\s*[^:]+: [0-9]+\\$");

    private SingleLottery lottery;

    @Before
    public void setUp() throws Exception {
        this.lottery = new SingleLottery(SilanisLottery.INITIAL_POT);
    }

    /**
     * Test purchaseTicket()
     */
    @Test
    public void testPurchaseTicket() throws Exception {
        final int initialPot = this.lottery.getPot();
        assertThat(initialPot).isEqualTo(SilanisLottery.INITIAL_POT);
        assertThat(this.lottery.purchaseTicket(TEST_BUYER_NAME)).isBetween(1, SilanisLottery.MAX_BALL);

        final int newPot = this.lottery.getPot();
        assertThat(newPot).as("Pot value was updated consecutively to ticket purchase").isEqualTo(initialPot + SilanisLottery.TICKET_PRICE);
    }

    /**
     * Test purchaseTicket() with null buyer name
     */
    @Test
    public void testPurchaseTicketNullBuyerName() throws Exception {
        assertThatExceptionOfType(InvalidBuyerNameException.class)
                .isThrownBy(() -> this.lottery.purchaseTicket(null));
    }

    /**
     * Test purchaseTicket() with empty buyer name
     */
    @Test
    public void testPurchaseTicketEmptyBuyerName() throws Exception {
        assertThatExceptionOfType(InvalidBuyerNameException.class)
                .isThrownBy(() -> this.lottery.purchaseTicket(""));
    }

    /**
     * Test purchaseTicket() with white-space buyer name
     */
    @Test
    public void testPurchaseTicketWhiteSpaceBuyerName() throws Exception {
        assertThatExceptionOfType(InvalidBuyerNameException.class)
                .isThrownBy(() -> this.lottery.purchaseTicket(" "));
    }

    /**
     * Test purchaseTicket() throws NoMoreTicketException
     */
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

    /**
     * Test purchaseTicket() throws SingleLotteryAlreadyDrawnException
     */
    @Test
    public void testPurchaseTicketThrowsSingleLotteryAlreadyDrawnException() throws Exception {
        // Test initialization
        this.lottery.drawLottery();

        // Test body
        assertThatExceptionOfType(SingleLotteryAlreadyDrawnException.class)
                .isThrownBy(() -> this.lottery.purchaseTicket(TEST_BUYER_NAME));
    }

    /**
     * Test drawLottery()
     */
    @Test
    public void testDrawLottery() throws Exception {
        final int[] currentDraw = this.lottery.drawLottery();
        assertThat(currentDraw)
                .hasSize(SilanisLottery.NB_WINNERS);
        assertThat(this.lottery.getWinners()).isNotNull();
    }

    /**
     * Test drawLottery() after being drawn already
     */
    @Test
    public void testDrawLotteryTwice() throws Exception {
        assertThatExceptionOfType(SingleLotteryAlreadyDrawnException.class)
                .isThrownBy(() -> {
                    this.lottery.drawLottery();
                    this.lottery.drawLottery();
                });
    }

    /**
     * Test drawLottery() with no buyer results in the pot not being updated
     */
    @Test
    public void testDrawLotteryNoWinnerImpliesPotNotUpdated() throws Exception {
        // test initialisation
        final int initialPot = this.lottery.getPot();

        // test body
        this.lottery.drawLottery();
        assertThat(this.lottery.getPot())
                .as("No winners implies the pot is not updated").isEqualTo(initialPot);
    }

    /**
     * Test drawLottery() updates the pot correctly
     */
    @Test
    public void testDrawLotteryPotUpdated() throws Exception {
        // Test initialization
        int currentPot = this.lottery.getPot();
        final int nbParticipants = 20;

        for (int i = 0; i < nbParticipants; i++) {
            this.lottery.purchaseTicket(TEST_BUYER_NAME);
            // Increase the current pot with ticket price
            currentPot += SilanisLottery.TICKET_PRICE;
            assertThat(this.lottery.getPot())
                    .as("The pot value after a ticket purchase")
                    .isEqualTo(currentPot);
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
        assertThat(this.lottery.getPot())
                .as("The pot value after retributing the winners")
                .isEqualTo(currentPot);
    }

    /**
     * Test computePrizes()
     */
    @Test
    public void testComputePrizes() throws Exception {
        // test initialisation
        final int initialPot = this.lottery.getPot();

        // test body
        final int[] prizes = this.lottery.computePrizes();
        assertThat(prizes)
                .hasSize(SilanisLottery.NB_WINNERS);
        assertThat(prizes)
                .as("75%, 15% and 10% of half the initial pot")
                .isEqualTo(new int[]{initialPot / 2 * 3 / 4, initialPot / 2 * 3 / 20, initialPot / 2 / 10});
    }

    /**
     * Test integer operations in computePrizes()
     */
    @Test
    public void testComputePrizesIntegerOperations() throws Exception {
        // test initialisation
        final int initialPot = 1013;
        this.lottery = new SingleLottery(initialPot);

        // test body
        final int[] prizes = this.lottery.computePrizes();
        assertThat(prizes)
                .hasSize(SilanisLottery.NB_WINNERS);
        assertThat(prizes)
                .as("75%, 15% and 10% of half the initial pot")
                .isEqualTo(new int[]{
                        (int) Math.floor(initialPot / 2. * .75),
                        (int) Math.floor(initialPot / 2. * .15),
                        (int) Math.floor(initialPot / 2. / 10.)
                });
    }

    /**
     * Test getWinners()
     */
    @Test
    public void testGetWinners() throws Exception {
        // Test initialization: we make sure every ticket is bought
        final Map<Integer, String> ticketBuyer = new HashMap<>();
        int i = 1;
        final String buyerNamePrefix = "BUYER";

        while (this.lottery.isTicketAvailable()) {
            final String buyerName = buyerNamePrefix + i;
            // map ticket number -> buyer's name
            ticketBuyer.put(this.lottery.purchaseTicket(buyerName), buyerName);
        }
        // computePrizes has to be called before drawLottery otherwise the values are wrong
        // (drawLottery updates the pot)
        final int[] prizes = this.lottery.computePrizes();
        final int[] results = this.lottery.drawLottery();

        assertThat(this.lottery.getWinners()).extracting("firstName", "prize")
                .as("We have the results, the winners' first names and prize must match")
                .containsExactly(
                        tuple(ticketBuyer.get(results[0]), prizes[0]),
                        tuple(ticketBuyer.get(results[1]), prizes[1]),
                        tuple(ticketBuyer.get(results[2]), prizes[2])
                );
    }

    /**
     * Test getWinners() while there is no ticket purchased which should
     * results in no winner.
     */
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

    /**
     * Test getWinners() has a null-element for each ball drawn which
     * ticket has not been bought
     */
    @Test
    public void testGetWinnersWithAtLeastANonWinner() throws Exception {
        // Test initialization
        for (int i = 0; i < SilanisLottery.NB_WINNERS - 1; i++) {
            this.lottery.purchaseTicket(TEST_BUYER_NAME);
            this.lottery.purchaseTicket(TEST_BUYER_NAME);
        }

        this.lottery.drawLottery();

        // NB_WINNERS - 1 tickets were bought and the lottery was drawn:
        // There will be at least one non-winning ball drawn
        // .contains(null): IDE complains that we provide null to a var-arg method
        assertThat(this.lottery.getWinners()).contains(new Winner[]{null});
    }

    /**
     * Test generateWinnersMessage()
     * <p>
     * This only tests the String produced is conform. The underlying
     * logic is tested in SilanisLotteryTest.testGetWinners().
     */
    @Test
    public void testGenerateWinnersMessage() throws Exception {
        // test initialisation
        this.lottery.drawLottery();

        // test body
        final String result = this.lottery.generateWinnersMessage();
        assertThat(result).isNotEmpty();
        final String[] splitResult = result.split(NEW_LINE_PATTERN);
        assertThat(splitResult)
                .as("Check there are two lines").hasSize(2);

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

    /**
     * Test generateWinnersMessage() throws SingleLotteryNotDrawnException
     */
    @Test
    public void testGenerateWinnersMessageThrowsSingleLotteryNotDrawnException() throws Exception {
        assertThatExceptionOfType(SingleLotteryNotDrawnException.class).isThrownBy(
                () -> this.lottery.generateWinnersMessage()
        );
        this.lottery.drawLottery();

        try {
            final String msg = this.lottery.generateWinnersMessage();
            LOGGER.info("Result:\n" + msg);
        } catch (final SingleLotteryNotDrawnException e) {
            fail("Unexpected exception: the lottery has been drawn");
        }
    }

    /**
     * Test generateWinnersMessage while there is no participant
     */
    @Test
    public void testGenerateWinnersMessageWithNoWinners() throws Exception {
        // test initialisation
        this.lottery.drawLottery();

        // test body
        final String result = this.lottery.generateWinnersMessage();
        final String[] splitResult = result.split(NEW_LINE_PATTERN);

        {
            // assertions on second line
            final String[] splitLine = splitResult[1].trim().split("\\t");
            assertThat(splitLine).hasSize(SilanisLottery.NB_WINNERS);
            for (final String winner : splitLine) {
                // each of the elements should display "No winner".
                assertThat(winner).containsPattern("No winner: [0-9]+\\$");
            }
        }
    }

}
