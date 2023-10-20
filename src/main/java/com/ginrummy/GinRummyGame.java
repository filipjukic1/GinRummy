package com.ginrummy;

import com.ginrummy.Enums.DrawChoice;
import com.ginrummy.Models.Card;
import com.ginrummy.Models.Deck;
import com.ginrummy.Models.Player;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

/**
 * The GinRummyGame class represents the logic for playing the Gin Rummy card game.
 * It manages player turns, gameplay, and calculates the deadwood count for each player.
 */
@Data
@NoArgsConstructor
public class GinRummyGame implements Serializable {
    public Deck deck;
    public Player player1;
    public Player player2;//Update only from remote
    public Boolean isGameOver = false;

    /**
     * Creates a new instance of the GinRummyGame class by copying the state of another GinRummyGame.
     *
     * @param ginRummyGame The GinRummyGame instance to copy the state from.
     */
    public GinRummyGame(GinRummyGame ginRummyGame) {
        this.deck = ginRummyGame.deck;
        this.player1 = ginRummyGame.player1;
        this.player2 = ginRummyGame.player2;
        this.isGameOver = ginRummyGame.isGameOver;
    }

    /**
     * Creates a new instance of the GinRummyGame class with the specified player names.
     * Initializes a new deck and two players with the provided names.
     *
     * @param player1Name The name of the first player.
     * @param player2Name The name of the second player.
     */
    public GinRummyGame(String player1Name, String player2Name) {
        this.deck = new Deck();
        this.player1 = new Player(player1Name);
        this.player2 = new Player(player2Name);
    }

    /**
     * Creates a new instance of the GinRummyGame class with the specified deck, and players object.
     * Initializes a new deck and two players with the provided names.
     *
     * @param deck The deck that use in gin rummy.
     * @param player1 The player1 object.
     * @param player2 The player2 object.
     */
    public GinRummyGame(Deck deck, Player player1, Player player2) {
        this.deck = deck;
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Starts the game and manages the sequence of player turns until a player wins or the game ends in a draw.
     * Player shuffle the deck, and draw som crads and sort them which are in his hand
     */
    public void play() {
        deck.shuffle();//52 cards

        /*Cards are drawn from the deck by players to add to their hands.*/
        IntStream.range(0, player1.PLAYER_HAND_SIZE)
                .forEach(i -> player1.drawFromStockPile(deck));//this draws card and add in hand

        /*sorting */
        player1.sortHand();
    }


    /**
     * Simulates a player's turn in the game.
     *
     * @param choice       The choice player selected to draw the card.
     * @param discardIndex Which Index in player hand to discard
     */
    private void playerTurn(DrawChoice choice, int discardIndex) {

        switch (choice) {
            case DRAW_FROM_STOCK_PILE://Draw from draw pile
                player1.drawFromStockPile(deck);
                break;
            case DRAW_FROM_DISCARD_PILE://Draw from discard pile
                player1.drawFromDiscardPile(deck);
                break;
        }

        player1.discardCard(discardIndex, deck);
        player1.sortHand();
    }


    /**
     * Check if the game is ended if stock pile is empty or player hand gets empty.
     *
     * @return the boolean that tells if game end or not
     */
    public boolean getIsGameOver() {
        if(deck.getStockPile().isEmpty() | player1.getHand().isEmpty() | getMeldCards().size() == 10)
            isGameOver = true;

        System.out.println(isGameOver);
        return isGameOver;
    }

    // A player can knock if they believe their unmatched cards have a lower total point value
    public boolean canKnock() {
        int deadwoodPoints = calculateDeadwoodAndMeldPoints()[0];
        return deadwoodPoints >= 10;//Minimum of 10 points in unmatched cards is required to knock
    }

    /**
     * Calculates and returns the deadwood points (unmatched cards) and meld points (matched cards) for a player's hand.
     * It checks the meld and remove them from player hand.
     *
     * @return The deadwood point and medl points for the player's hand.
     */
    private int[] calculateDeadwoodAndMeldPoints() {

        List<Card> copyHand = new ArrayList<>(player1.getHand());
        List<Card> meld = getMeldCards();
        copyHand.removeAll(meld);

        int deadwoodPoints = 0;
        // Calculate the points for unmatched cards (deadwood)
        for (Card card : copyHand) {
            deadwoodPoints += card.getRank().getValue();
        }

        int meldPoints = 0;
        for (Card card : meld) {
            meldPoints += card.getRank().getValue();
        }

        return new int[] {deadwoodPoints, meldPoints};
    }


    /**
     * Find the Meld cards from the player hand
     *
     * @return the list of meld cards.
     */
    public List<Card> getMeldCards() {
        // Create a copy of the hand to avoid modifying the original hand
        List<Card> copyHand = new ArrayList<>(player1.getHand());
        List<Card> meld = new ArrayList<>();

        // Check for and remove valid sets (melds) from the copied hand
        for (int i = 0; i < copyHand.size() - 2; i++) {
            Card currentCard = copyHand.get(i);

            int j = i + 1;

            while (j < copyHand.size()) {
                Card nextCard = copyHand.get(j);
                //same suit
                if (Objects.equals(currentCard.getSuit().getName(), nextCard.getSuit().getName())) {
                    if (currentCard.getRank().getValue() + 1 == nextCard.getRank().getValue()) {
                        j++;
                        continue;
                    }
                }

                break;
            }

            if (j <= copyHand.size() && j - i >= 3) {
                meld.addAll(copyHand.subList(i, j + 1));
                i = j;
            }
        }

        copyHand.removeAll(meld);

        // check the sequence for different suit
        for (int i = 0; i < copyHand.size() - 2; i++) {
            Card currentCard = copyHand.get(i);

            int j = i + 1;

            while (j < copyHand.size()) {
                Card nextCard = copyHand.get(j);
                //not same suit
                if (!Objects.equals(currentCard.getSuit().getName(), nextCard.getSuit().getName())) {
                    if (Objects.equals(currentCard.getRank().getValue(), nextCard.getRank().getValue())) {//same rank
                        j++;
                        continue;
                    }
                }

                break;
            }

            if (j <= copyHand.size() && j - i >= 3) {
                meld.addAll(copyHand.subList(i, j));
                i = j;
            }
        }

        return meld;
    }

    public void UpdateCardDiscardableStatus() {
        List<Card> meld = getMeldCards();
        player1.getHand().stream()
                .filter(meld::contains)
                .forEach(card -> card.setDiscardable(false));
    }

    void updateScore(){
        player1.setScore(player1.getScore() + calculateDeadwoodAndMeldPoints()[1]);
    }
}
