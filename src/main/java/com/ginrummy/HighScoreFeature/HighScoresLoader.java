package com.ginrummy.HighScoreFeature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ginrummy.Models.Player;
import com.ginrummy.XMLLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The HighScoresLoader class provides methods to load and save high scores for the Gin Rummy game.
 */
public class HighScoresLoader {
    private static final String FILE_NAME = "highscores.xml";
    private static final int LIMIT_HIGHEST_SCORE_LIST = 1;

    /**
     * Loads the player data (high scores) from an XML file.
     *
     * @return A list of Player objects representing high scores.
     * @throws IOException If an error occurs while reading the XML file.
     */
    public static List<Player> loadPlayerData() throws IOException {
        return XMLLoader.loadMovesFromXML(FILE_NAME);
    }

    /**
     * Saves the player's score to the high scores list and updates the XML file.
     *
     * @param player The Player object representing the player whose score is to be saved.
     */
    public static void savePlayerData(Player player) {
        try {
            List<Player> players = loadPlayerData();
            players.add(player);

            List<Player> data = players.stream()
                    .sorted(Comparator.comparingLong(Player::getScore).reversed())
                    .limit(LIMIT_HIGHEST_SCORE_LIST)
                    .collect(Collectors.toList());
            data.stream().forEach(System.out::println);
            XMLLoader.saveMovesToXML("highscores.xml", data);
        } catch(Exception e) {
            // If there was an error loading existing data, create a new list with the current player's score.
            List<Player> players = new ArrayList<>();
            players.add(player);
            XMLLoader.saveMovesToXML("highscores.xml", players);
        }
    }
}
