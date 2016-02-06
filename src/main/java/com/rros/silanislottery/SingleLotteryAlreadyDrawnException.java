package com.rros.silanislottery;

/**
 * The lottery has already been drawn
 */
public class SingleLotteryAlreadyDrawnException extends IllegalStateException {
    public SingleLotteryAlreadyDrawnException() {
        super("The single lottery has already been drawn.");
    }
}
