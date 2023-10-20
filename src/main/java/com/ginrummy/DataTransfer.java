package com.ginrummy;

import com.ginrummy.Models.Deck;
import com.ginrummy.Models.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * It is used to transfer data between multiple players remotely
 */
@AllArgsConstructor
public class DataTransfer implements Serializable {
    Player player1;
    Player player2;
    Deck deck;
    Boolean isGameOver;
    Integer score;
    String name;
}
