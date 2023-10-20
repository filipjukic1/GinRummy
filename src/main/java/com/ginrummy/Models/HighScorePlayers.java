package com.ginrummy.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Class to represent List of players to be added in High score category
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HighScorePlayers {
    List<Player> players;
}
