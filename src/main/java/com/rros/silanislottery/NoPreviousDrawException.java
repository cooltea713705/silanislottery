package com.rros.silanislottery;

/**
 * There has been no previous draw.
 */
public class NoPreviousDrawException extends Exception {
    public NoPreviousDrawException() {
        super("There is not any previous draw.");
    }
}
