package com.rros.draw;

import com.rros.silanislottery.SilanisLottery;

import java.util.Random;

/**
 * Drawable integer values between 1 and SilanisLottery.MAX_BALL
 */
public class DrawableInteger {

    /**
     * Current drawables, currentMax is the index of the last drawn element if there is one (otherwise its value is drawables.length)
     */
    private final int[] drawables = new int[SilanisLottery.MAX_BALL];
    /**
     * Random used for the draws.
     * <p>
     * Nota: java.util.Random is not cryptographically secure, but we assume Tommy is ok with that for the Silanis lottery, otherwise use SecureRandom
     */
    private final Random random;
    /**
     * Current max index for the draw
     * <p>
     * Draws are indices in [O, currentMax[ elements of this.drawables.
     */
    private int currentMax = SilanisLottery.MAX_BALL;

    /**
     * Initialize the drawable collection of integer values
     */
    public DrawableInteger() {
        this(new Random());
    }

    /**
     * Initialize the drawable collection of integer values
     *
     * @param random input random, used for debugging (reproducible draws)
     */
    DrawableInteger(final Random random) {
        this.random = random;

        for (int i = 0; i < SilanisLottery.MAX_BALL; i++) {
            this.drawables[i] = i + 1; // tickets and ball are 1-indexed collections
        }
    }

    /**
     * Draw one element in the bag and prepare the next draw is without replacement.
     * <p>
     * Ref: This method implements the algorithm described in http://stackoverflow.com/a/196065
     *
     * @return drawn object, in this case an integer value
     * @throws NoAvailableDrawWithoutReplacementException no more available draw
     */
    public int drawWithoutReplacement() throws NoAvailableDrawWithoutReplacementException {
        // pick an index between 0 (included) and this.currentMax (excluded)
        if (!isDrawWithoutReplacementAvailable()) {
            throw new NoAvailableDrawWithoutReplacementException();
        }

        final int currentDrawIndex = this.random.nextInt(this.currentMax);
        final int currentDraw = this.drawables[currentDrawIndex];

        // swap this.drawables[currentDrawIndex] and this.drawables[this.currentMax-1]
        final int tmp = this.drawables[currentDrawIndex];
        this.drawables[currentDrawIndex] = this.drawables[this.currentMax - 1];
        this.drawables[this.currentMax - 1] = tmp;

        // decrease this.currentMax: this way the last elements from this.drawables are left untouched.
        this.currentMax--;

        return currentDraw;
    }

    /**
     * @return true if a draw without replacement is available, false otherwise
     */
    public boolean isDrawWithoutReplacementAvailable() {
        // no more draw available when currentMax is equal to or lesser than 0
        return this.currentMax > 0;
    }

}
