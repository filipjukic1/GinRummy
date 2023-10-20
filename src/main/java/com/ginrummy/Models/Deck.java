package com.ginrummy.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ginrummy.Enums.Rank;
import com.ginrummy.Enums.Suit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Deck class represents a standard deck of playing cards.
 * It contains methods to initialize, shuffle, and deal cards.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deck implements Serializable {//Deck: Stack of Cards
    public List<Card> stockPile;//stack or deck of cards
    public List<Card> discardPile;//stack or deck of cards

    /**
     * Initializes a new deck of playing cards, including all suits and ranks.
     */
    public Deck() {
        stockPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        // Initialize the deck with cards 52 cards
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                stockPile.add(new Card(rank, suit));
            }
        }
    }

    /**
     * Random Shuffle Stack of Cards (Deck)
     */
    public void shuffle() {
        Collections.shuffle(stockPile);
    }

    /**
     * Draw a card from the deck (Stock Pile)
     * @return the card that is drawn from the deck (Stock Pile)
     */
    public Card drawFromStockPile() {
        if (!stockPile.isEmpty()) {
            return stockPile.remove(stockPile.size() - 1);// Draws and removes the top card from the deck
        }
        return null;
    }

    /**
     * Draw a card from the deck (Discard Pile)
     * @return the card that is drawn from the deck (Discard Pile)
     */
    public Card drawFromDiscardPile() {
        if (!discardPile.isEmpty()) {
            return discardPile.remove(discardPile.size() - 1);// Draws and removes the top card from the deck
        }
        return null;
    }

    /**
     * Add card to discard pile
     *
     * @param card The card to be added in discard pile
     */
    public void addCardToDiscardPile(Card card) {
        discardPile.add(card);
    }

    /**
     * It shows out top card in stock pile
     *
     * @return The top card in stock pile
     */
    public Card getTopStockPileCard() {
        return stockPile.get(stockPile.size() - 1);
    }

    /**
     * It shows out top card in discard pile
     *
     * @return The top card in discard pile
     */
    public Card getTopDiscardPileCard() {
        return discardPile.get(discardPile.size() - 1);
    }
}
