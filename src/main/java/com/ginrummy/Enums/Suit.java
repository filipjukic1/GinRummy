package com.ginrummy.Enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * The Suit enum represents the suits of playing cards in a standard deck.
 */
@Getter
public enum Suit implements Serializable {
    CLUB("clubs", 'C', "♣"),
    DIAMOND("diamonds", 'D', "♦"),
    HEART("hearts", 'H', "♥"),
    SPADE("spades", 'S', "♠");

    private final String name;
    private final char character;//first letter of suit name
    private final String symbol;

    Suit(String name, char character, String symbol) {
        this.name = name;
        this.character = character;
        this.symbol = symbol;
    }

    public String toString() {
        return name;
    }
}
