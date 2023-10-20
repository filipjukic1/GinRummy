package com.ginrummy.Enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * The Rank enum represents the ranks of playing cards in a standard deck.
 */
@Getter
public enum Rank implements Serializable {
    ACE (1, 1, "ace", 'A'),
    TWO (2, 2, "2", '2'),
    THREE (3, 3, "3", '3'),
    FOUR (4, 4, "4", '4'),
    FIVE (5, 5, "5", '5'),
    SIX (6, 6, "6", '6'),
    SEVEN (7, 7, "7", '7'),
    EIGHT (8, 8, "8", '8'),
    NINE (9, 9, "9", '9'),
    TEN (10, 10, "10", 'T'),//point value for K,Q,J,10 is same (10)
    JACK (11, 10, "jack", 'J'),//point value for K,Q,J,10 is same (10)
    QUEEN (12, 10, "queen", 'Q'),//point value for K,Q,J,10 is same (10)
    KING (13, 10, "king", 'K');//point value for K,Q,J,10 is same (10)

    private final int order;
    private final Integer value;//points
    private final String name;
    private final char symbol;

    Rank(int order, int value, String name, char symbol) {
        this.order = order;
        this.value = value;
        this.name = name;
        this.symbol = symbol;
    }

    public String toString() {
        return name;
    }
}
