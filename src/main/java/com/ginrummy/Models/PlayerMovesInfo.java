package com.ginrummy.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * PlayerMovesInfo keep records of the moves done by the player while playing the game.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerMovesInfo implements Serializable {
    public String card;
    public String deck;//draw pile, stock pile

    /**
     * To get the moves of the player in the text format
     *
     * @param player The player for which moves are needed
     * @return The string that contains player moves the card name and from which pile of deck it is taken
     */
    public static String getMoves(Player player) {
        StringBuilder movesInfo = new StringBuilder();
        for (PlayerMovesInfo move : player.getAllPlayerOldMoves()) {
            movesInfo.append("Card: ").append(move.card).append(" - Deck: ").append(move.deck).append("\n");
        }

        return movesInfo.toString();
    }
}
