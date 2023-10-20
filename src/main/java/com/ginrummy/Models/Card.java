package com.ginrummy.Models;

import com.ginrummy.Enums.Rank;
import com.ginrummy.Enums.Suit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The Card class represents a playing card with a rank and a suit.
 */
@Data
@NoArgsConstructor
public class Card implements Serializable {
    public static final Integer CARD_WIDTH = 100;
    public static final Integer CARD_HEIGHT = 150;

    public Suit suit;// Hearts, Diamonds, Spade, Club
    public Rank rank;// 2, 3, 4, 5, 6, 7, 8, 9, 10, Jack, Queen, King, Ace, Joker

    public boolean isDiscardable = true;

    /**
     * Constructs a card with the specified rank and suit.
     *
     * @param rank The rank of the card.
     * @param suit The suit of the card.
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Returns a string representation of the card.
     *
     * @return A string representation of the card, e.g., "ace_of_hearts".
     */
    @Override
    public String toString() {
        return rank + "_of_" + suit;
    }
}
