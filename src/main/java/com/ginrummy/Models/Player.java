package com.ginrummy.Models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Player class represents a player in the Gin Rummy game.
 * It stores information about the player's name, score, and other game-related data.
 */
@Data
@NoArgsConstructor
public class Player implements Serializable {
    public static Integer PLAYER_HAND_SIZE = 10;// Each player can have 10 cards

    private String name;// player's name
    private List<Card> hand = new ArrayList<>();// list representing the player's hand of cards.
    private Integer score = 0;

    private List<PlayerMovesInfo> allPlayerOldMoves = new ArrayList<>();//just for highScores

    /**
     * Initializes a new player with the given name and an initial score of 0.
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
    }

    public Player(String name, Integer score) {
        this.name = name;
        this.score = score;
    }

    public void addToDiscardPile(Card card, Deck deck) {
        // Remove a card from the player hand and add it in discard pile.
        hand.remove(card);
        deck.addCardToDiscardPile(card);
        sortHand();
        allPlayerOldMoves.add(new PlayerMovesInfo(card.toString(), "added to discard pile"));
    }

    /**
     * Draws a card from the game's deck Stock Pile and adds it to the player's hand.
     * Also add the move in OldPlayerMoves
     *
     * @param deck The game's deck of cards (Stock Pile) from which the player draws a card.
     */
    public void drawFromStockPile(Deck deck) {
        // Draw a card from the deck and add it to the player's hand
        Card card = deck.drawFromStockPile();
        if (card != null) {
            hand.add(card);
            sortHand();
            allPlayerOldMoves.add(new PlayerMovesInfo(card.toString(), "draw from stock pile"));
        }
    }

    /**
     * Draws a card from the game's deck Discard Pile and adds it to the player's hand.
     * Also add the move in OldPlayerMoves
     *
     * @param deck The game's deck of cards (Discard Pile) from which the player draws a card.
     */
    public void drawFromDiscardPile(Deck deck) {
        // Draw a card from the deck and add it to the player's hand
        Card card = deck.drawFromDiscardPile();
        if (card != null) {
            hand.add(card);
            sortHand();
            allPlayerOldMoves.add(new PlayerMovesInfo(card.toString(), "draw from discard pile"));
        }
    }

    /**
     * Draws a card from the game's deck Discard Pile and adds it to the player's hand.
     * Do the same as above (but It is not in use).
     *
     * @param discardIndex The index of hand card which is to be discarded
     * @param deck The game's deck of cards (Discard Pile) from which the player draws a card.
     */
    public void discardCard(int discardIndex, Deck deck) {//Not In Use as PlayerTurn method is Not in use too.
        Card card = hand.remove(discardIndex);
        deck.getDiscardPile().add(card);
        sortHand();
    }

    /**
     * Calculate Scores For the player, It calculates the rank of each card in hand
     *
     * @param hand The hand of the player having cards
     * @return The scores computed
     */
    private int calculateScore(List<Card> hand) {
        // Score is the sum of card values.
        int score = 0;
        for (Card card : hand) {
            score += card.getRank().getValue();
        }
        return score;
    }

    /**
     * The player sort the card in his hand
     */
    public void sortHand() {
        hand = hand.stream()
                .sorted(Comparator.comparingInt(o -> o.getRank().getValue()))
                .collect(Collectors.toList());
    }
}

