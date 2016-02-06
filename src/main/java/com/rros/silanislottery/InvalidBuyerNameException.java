package com.rros.silanislottery;

/**
 * The buyer's name is invalid.
 */
public class InvalidBuyerNameException extends Exception {
    public InvalidBuyerNameException(String message) {
        super(message);
    }
}
