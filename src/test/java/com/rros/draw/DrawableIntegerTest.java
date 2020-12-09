package com.rros.draw;

import com.rros.silanislottery.SilanisLottery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for com.rros.draw.DrawableInteger
 */
public class DrawableIntegerTest {

    private DrawableInteger drawableInteger;

    @BeforeEach
    public void setUp() {
        this.drawableInteger = new DrawableInteger();
    }

    @Test
    public void testDraw() throws Exception {
        final Set<Integer> draws = new HashSet<>();

        while (this.drawableInteger.isDrawWithoutReplacementAvailable()) {
            final int draw = this.drawableInteger.drawWithoutReplacement();
            assertThat(draw).isBetween(1, SilanisLottery.MAX_BALL);
            assertThat(draws).doesNotContain(draw);
            draws.add(draw);
        }
        assertThat(draws).hasSize(SilanisLottery.MAX_BALL)
                .doesNotHaveDuplicates();
    }

    /**
     * Test that before the timeout we have a draw that is not 1, 2, ..., MAX
     */
    @Test
    @Timeout(1)
    public void testDrawIsRandom() throws Exception {
        final List<Integer> nonRandom = new ArrayList<>();
        for (int i = 1; i < SilanisLottery.MAX_BALL; i++) {
            nonRandom.add(i);
        }

        List<Integer> draws;
        do {
            draws = new ArrayList<>();
            this.fillDraws(draws);
        } while (draws.equals(nonRandom));
    }

    @Test
    public void testTwoDraws() throws Exception {
        // Test initialisation

        /*
         * find a seed for which the draw differs: we loop until the
         * first index drawn (Random(seed).nextInt(SilanisLottery.MAX_BALL))
         * is different from the first index drawn for
         * Random(seed + 1).SilanisLottery.MAX_BALL
         */
        long seed;
        do {
            seed = System.currentTimeMillis();
        }
        while (new Random(seed).nextInt(SilanisLottery.MAX_BALL) == new Random(seed + 1).nextInt(SilanisLottery.MAX_BALL));

        this.drawableInteger = new DrawableInteger(new Random(seed));

        final List<Integer> draws = new ArrayList<>();
        this.fillDraws(draws);

        final List<Integer> secondDraws = new ArrayList<>();
        this.drawableInteger = new DrawableInteger(new Random(seed + 1));
        this.fillDraws(secondDraws);
        assertThat(secondDraws)
                .as("The sequence of draws is supposed")
                .isNotEqualTo(draws);
    }

    @Test
    public void testTwoDrawsInitializedWithTheSameSeed() throws Exception {
        final long seed = System.currentTimeMillis();
        this.drawableInteger = new DrawableInteger(new Random(seed));

        final List<Integer> draws = new ArrayList<>();
        this.fillDraws(draws);

        final List<Integer> secondDraws = new ArrayList<>();
        this.drawableInteger = new DrawableInteger(new Random(seed));
        this.fillDraws(secondDraws);
        // secondDraws will be strictly equal to this.drawableInteger
        assertThat(secondDraws).isEqualTo(draws);
    }

    /**
     * Draw until there is no more available draw
     *
     * @param draws input list
     */
    private void fillDraws(List<Integer> draws) {
        try {
            while (this.drawableInteger.isDrawWithoutReplacementAvailable()) {
                final int draw = this.drawableInteger.drawWithoutReplacement();
                draws.add(draw);
            }
        } catch (final NoAvailableDrawWithoutReplacementException e) {
            fail("Unexpected exception: we stop when there is no more available draw.", e);
        }
    }

    @Test
    public void testDrawNoAvailableDrawException() throws Exception {
        try {
            while (this.drawableInteger.isDrawWithoutReplacementAvailable()) {
                this.drawableInteger.drawWithoutReplacement();
            }
        } catch (final NoAvailableDrawWithoutReplacementException e) {
            fail("Unexpected exception", e);
        }
        // The SilanisLottery.MAX_BALL + 1 call (and all subsequent calls) are expected to throw an exception
        assertThatExceptionOfType(NoAvailableDrawWithoutReplacementException.class).isThrownBy(() -> this.drawableInteger.drawWithoutReplacement());
        assertThatExceptionOfType(NoAvailableDrawWithoutReplacementException.class).isThrownBy(() -> this.drawableInteger.drawWithoutReplacement());
        // assertThatThrownBy(() -> this.drawableInteger.draw()).isInstanceOf(NoAvailableDrawWithoutReplacementException.class);
    }
}
