package com.ginrummy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ginrummy.Models.HighScorePlayers;
import com.ginrummy.Models.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The XMLLoader class provides methods for saving and loading Gin Rummy game data and player moves
 * to and from XML files.
 */
public class XMLLoader {

    private static final String saveFile = "gin_rummy_moves.xml";

    /**
     * Saves a Gin Rummy game to an XML file.
     *
     * @param filePath      The path to the XML file where the game will be saved.
     * @param ginRummyGame  The Gin Rummy game to be saved.
     * @throws IOException  If there is an issue with writing to the XML file.
     */
    public static void saveGameToXML(String filePath, GinRummyGame ginRummyGame) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(new File(filePath), ginRummyGame);

        System.out.println("Game moves saved to gin_rummy_moves.xml");
    }

    /**
     * Loads a Gin Rummy game from an XML file.
     *
     * @param filePath      The path to the XML file from which the game will be loaded.
     * @return              The loaded Gin Rummy game.
     * @throws IOException  If there is an issue with reading from the XML file.
     */
    public static GinRummyGame loadGameFromXML(String filePath) throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(new File(filePath), GinRummyGame.class);
    }

    /**
     * Saves a list of player moves to an XML file.
     *
     * @param filePath      The path to the XML file where player moves will be saved.
     * @param players       The list of players whose moves will be saved.
     */
    public static void saveMovesToXML(String filePath, List<Player> players) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.writeValue(new File(filePath), new HighScorePlayers(players));

            System.out.println("Game moves saved to highscores.xml");
        } catch (IOException e) {}
    }

    /**
     * Loads a list of player moves from an XML file.
     *
     * @param filePath      The path to the XML file from which player moves will be loaded.
     * @return              The loaded list of players with their moves.
     * @throws IOException  If there is an issue with reading from the XML file.
     */
    public static List<Player> loadMovesFromXML(String filePath) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(new File(filePath), HighScorePlayers.class).getPlayers();
    }
}
