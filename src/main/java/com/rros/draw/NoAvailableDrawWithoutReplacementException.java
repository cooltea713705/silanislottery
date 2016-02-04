package com.rros.draw;

/**
 * No more draw is available.
 */
public class NoAvailableDrawWithoutReplacementException extends Exception {

    public NoAvailableDrawWithoutReplacementException() {
        super("There is no more available draw without replacement.");
    }

}
