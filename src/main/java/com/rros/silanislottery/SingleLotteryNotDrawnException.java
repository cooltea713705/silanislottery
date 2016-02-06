package com.rros.silanislottery;

/**
 * The lottery has not been drawn yet
 */
public class SingleLotteryNotDrawnException extends Exception {
    public SingleLotteryNotDrawnException(String message) {
        super(message);
    }
}
