package com.rros.silanislottery;

/**
 * Lottery winner
 */
public class Winner {
    /**
     * Winning price
     */
    private final int price;

    /**
     * Winner's first name (might be null)
     */
    private final String firstName;

    /**
     * Instantiate a winner to be displayed
     *
     * @param firstName winner's first name, might be null
     * @param price     winning price
     */
    public Winner(final String firstName, final int price) {
        this.firstName = firstName;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getFirstName() {
        return firstName;
    }
}
