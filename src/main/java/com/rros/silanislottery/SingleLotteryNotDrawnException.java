package com.rros.silanislottery;

/**
 * The lottery has not been drawn yet
 */
public class SingleLotteryNotDrawnException extends IllegalStateException {
    public SingleLotteryNotDrawnException() {
        super("The single lottery has not been drawn yet.");
    }
}
