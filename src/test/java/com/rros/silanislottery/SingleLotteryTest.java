package com.rros.silanislottery;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Test class for SingleLottery
 */
public class SingleLotteryTest {


    private SingleLottery lottery;

    @Before
    public void setUp() throws Exception {
        this.lottery = new SingleLottery(SilanisLottery.INITIAL_POT);
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
        final int[] results = this.lottery.drawLottery();
        final int[] prizes = this.lottery.computePrizes();
        assertThat(this.lottery.getWinners()).extracting("name", "prize")
                .as("We have the results, the winners' names and prize must match")
                .containsExactly(
                        tuple(ticketBuyer.get(results[0]), prizes[0]),
                        tuple(ticketBuyer.get(results[1]), prizes[1]),
                        tuple(ticketBuyer.get(results[2]), prizes[2])
                );
    }

}
