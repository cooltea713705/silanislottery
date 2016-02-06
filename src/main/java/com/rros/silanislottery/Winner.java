package com.rros.silanislottery;

/**
 * Lottery winner
 */
public class Winner {
    /**
     * Winning prize
     */
    private final int prize;

    /**
     * Winner's first name (might be null)
     */
    private final String firstName;

    /**
     * Instantiate a winner to be displayed
     *
     * @param firstName winner's first name, might be null
     * @param prize     winning prize
     */
    public Winner(final String firstName, final int prize) {
        this.firstName = firstName;
        this.prize = prize;
    }

    public int getPrize() {
        return prize;
    }

    public String getFirstName() {
        return firstName;
    }
}
