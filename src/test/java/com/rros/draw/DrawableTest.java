package com.rros.draw;

import com.rros.silanislottery.SilanisLottery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for com.rros.draw.Drawable
 */
public class DrawableTest {

    private final static Logger LOGGER = Logger.getGlobal();
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

    @Ignore
    public void testDrawIsRandom() throws Exception {
        final List<Integer> draws = new ArrayList<>();

        this.fillDraws(draws);

        LOGGER.info(() -> "Draws should be a random list of ints: " + draws);
        // There is a chance the series of draws 1, 2, 3, 4..., 50 comes up (the probability is small)
        // TODO: Something we could try and assert is the distribution of draws orders
    }

    @Ignore
    public void testTwoDraws() throws Exception {
        final long seed = System.currentTimeMillis();
        this.drawable = new Drawable(new Random(seed));

        final List<Integer> draws = new ArrayList<>();
        this.fillDraws(draws);

        final List<Integer> secondDraws = new ArrayList<>();
        this.drawable = new Drawable(new Random(seed + 1));
        this.fillDraws(secondDraws);
        // TODO secondDraws will **most likely** be different from this.drawable
        assertThat(secondDraws).isNotEqualTo(draws);
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
