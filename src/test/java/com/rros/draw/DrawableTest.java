package com.rros.draw;

import com.rros.silanislottery.SilanisLottery;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for com.rros.draw.Drawable
 */
public class DrawableTest {

    private Drawable drawable;

    @Before
    public void setUp() {
        drawable = new Drawable();
    }

    @Test
    public void testDraw() throws Exception {
        final Set<Integer> draws = new HashSet<>();

        while (drawable.isDrawWithoutReplacementAvailable()) {
            final int draw = drawable.drawWithoutReplacement();
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
    @Test(timeout = 1000L)
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
        // Test initialization

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

        this.drawable = new Drawable(new Random(seed));

        final List<Integer> draws = new ArrayList<>();
        this.fillDraws(draws);

        final List<Integer> secondDraws = new ArrayList<>();
        this.drawable = new Drawable(new Random(seed + 1));
        this.fillDraws(secondDraws);
        assertThat(secondDraws)
                .as("The sequence of draws is supposed")
                .isNotEqualTo(draws);
    }

    @Test
    public void testTwoDrawsInitializedWithTheSameSeed() throws Exception {
        final long seed = System.currentTimeMillis();
        this.drawable = new Drawable(new Random(seed));

        final List<Integer> draws = new ArrayList<>();
        this.fillDraws(draws);

        final List<Integer> secondDraws = new ArrayList<>();
        this.drawable = new Drawable(new Random(seed));
        this.fillDraws(secondDraws);
        // secondDraws will be strictly equal to this.drawable
        assertThat(secondDraws).isEqualTo(draws);
    }

    /**
     * Draw until there is no more available draw
     *
     * @param draws input list
     */
    private void fillDraws(List<Integer> draws) {
        try {
            while (this.drawable.isDrawWithoutReplacementAvailable()) {
                final int draw = this.drawable.drawWithoutReplacement();
                draws.add(draw);
            }
        } catch (final NoAvailableDrawWithoutReplacementException e) {
            fail("Unexpected exception: we stop when there is no more available draw.", e);
        }
    }

    @Test
    public void testDrawNoAvailableDrawException() throws Exception {
        try {
            while (this.drawable.isDrawWithoutReplacementAvailable()) {
                this.drawable.drawWithoutReplacement();
            }
        } catch (final NoAvailableDrawWithoutReplacementException e) {
            fail("Unexpected exception", e);
        }
        // The SilanisLottery.MAX_BALL + 1 call (and all subsequent calls) are expected to throw an exception
        assertThatExceptionOfType(NoAvailableDrawWithoutReplacementException.class).isThrownBy(() -> drawable.drawWithoutReplacement());
        assertThatExceptionOfType(NoAvailableDrawWithoutReplacementException.class).isThrownBy(() -> drawable.drawWithoutReplacement());
        // assertThatThrownBy(() -> drawable.draw()).isInstanceOf(NoAvailableDrawWithoutReplacementException.class);
    }
}
