package com.ginrummy;

import com.ginrummy.ChatFeatureRMI.ChatClient;
import com.ginrummy.ChatFeatureRMI.ChatServer;
import com.ginrummy.HighScoreFeature.HighScoresLoader;
import com.ginrummy.Interfaces.IChatServer;
import com.ginrummy.Models.Card;
import com.ginrummy.Models.Deck;
import com.ginrummy.Models.PlayerMovesInfo;
import com.ginrummy.Models.Player;
import com.ginrummy.NetworkSettingsJNDI.NetworkSettings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static com.ginrummy.Models.Card.CARD_HEIGHT;
import static com.ginrummy.Models.Card.CARD_WIDTH;
import static com.ginrummy.Models.Player.PLAYER_HAND_SIZE;
import static com.ginrummy.reflectionDocGeneration.DocGen.generateDocumentation;

/**
 * The GinRummyUI class represents the user interface for the Gin Rummy game.
 * It includes methods for initializing and managing the game's graphical user interface.
 * And Threads for transfering data.
 */
public class GinRummyUI extends Application {//Handles the graphical user interface (GUI) for the game.
    private final String CARDS_PATH = "Cards-Images/";
    GinRummyGame ginRummyGame;
    Stage primaryStage;
    private List<ImageView> player1Cards = new ArrayList<>();
    private List<ImageView> player2Cards = new ArrayList<>();
    private List<ImageView> deckCards = new ArrayList<>();

    private String player1Name, player2Name;
    Label player1Label, player2Label;
    private final Scene playerNameScene = createPlayerNameSelectionScene();
    private Scene gameplayScene, highScoreScene, movesDisplayScene;

    private ListView<String> chatListView;
    private TextField messageField;

    TextArea textAreaPlayer1 = new TextArea();
    TextArea textAreaPlayer2 = new TextArea();

    ChatClient chatClient;
    IChatServer chatServer;

    Socket clientSocket;
    ServerSocket serverSocket = null;

    ObjectOutputStream outputStream = null;
    boolean loadedGame = false;

    /**
     * Starts the Gin Rummy game user interface.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     * @throws IOException       If an I/O error occurs while initializing the UI components.
     * @throws NotBoundException If a remote object is not found while initializing the UI.
     */
    @Override
    public void start(Stage primaryStage) throws IOException, NotBoundException {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Gin Rummy");

        Thread docsGen = new Thread(() -> {
            try {
                generateDocumentation(GinRummyGame.class);
                generateDocumentation(Player.class);
                generateDocumentation(Deck.class);
                generateDocumentation(DataTransfer.class);
                generateDocumentation(Card.class);
                generateDocumentation(NetworkSettings.class);
                generateDocumentation(HighScoresLoader.class);
                generateDocumentation(ChatServer.class);
                generateDocumentation(ChatClient.class);
                generateDocumentation(GinRummyUI.class);
                generateDocumentation(XMLLoader.class);
            } catch (IOException e) {

            }

        });
        docsGen.start();


        initialization();
        initUI(textAreaPlayer1);
        initUI(textAreaPlayer2);

        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                handleOnClose(event);
            }

        });

        primaryStage.setScene(playerNameScene);
        primaryStage.show();
    }

    /**
     * Initialize the chat client so that it can communicate with server
     *
     * @throws IOException
     * @throws NotBoundException
     */
    private void initialization() throws IOException, NotBoundException {
        chatClient = new ChatClient(player1Name) {
            @Override
            public void receiveMessage(String message) throws RemoteException {
                Platform.runLater(() -> {
                    chatListView.getItems().add(message);
                });
            }
        };

        chatServer = ChatServer.getIChatServer();
        chatServer.registerClient(chatClient);
    }

    /**
     * Initialize the text area that is used for scoring
     *
     * @param textArea
     * @throws IOException
     * @throws NotBoundException
     */
    private void initUI(TextArea textArea) throws IOException, NotBoundException {
        textArea.setMaxWidth(20);
        textArea.setMaxHeight(5);
        textArea.setPrefColumnCount(10);
        textArea.setPrefRowCount(1);
    }

    /**
     * Creates a JavaFX scene for the player name selection it also contains start game button
     * Also contains show High score and button tha =t helps in loading the game from a file as well.
     *
     * @return A JavaFX Scene object representing the player name selection scene.
     */
    public Scene createPlayerNameSelectionScene() {
        TextField playerNameTextField = new TextField();
        Button startGameButton = new Button("Start New Game");
        Button showHighScoresButton = new Button("Show High Scores");
        Button loadGameButton = new Button("Load Game");

        // Event handler for the "Start Game" button
        startGameButton.setOnAction(event -> {
            loadedGame = false;
            String playerName = playerNameTextField.getText();
            if (!playerName.isEmpty()) {
                player1Name = playerName;

                switchToGameplayScene();

                Thread peerThread = new Thread(() -> {
                    try {
                        communicateWithPeerWithServerThread();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                peerThread.start();
            }
        });

        showHighScoresButton.setOnAction(event -> {
            switchToHighScoreScene();
        });

        loadGameButton.setOnAction(event -> {
            loadedGame = true;
            switchToGameplayScene();
            Thread peerThread = new Thread(() -> {
                try {
                    communicateWithPeerWithServerThread();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            peerThread.start();
        });

        startGameButton.setMaxWidth(150);
        showHighScoresButton.setMaxWidth(150);
        loadGameButton.setMaxWidth(150);
        playerNameTextField.setMaxWidth(200);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(playerNameTextField, startGameButton, showHighScoresButton, loadGameButton);

        return new Scene(layout, 400, 200);
    }

    /**
     * Update the UI of Player
     * @param player The player for which UI update required
     * @param playerCards UI of player hand card
     * @param textArea score Field
     */
    void UpdateThePlayerHandUI(Player player, List<ImageView> playerCards, TextArea textArea) {
        Platform.runLater(() -> {

            for (int i = 0; i < PLAYER_HAND_SIZE; i++) {
                Image image;
                if(i < player.getHand().size()) {
                    String imagePath = CARDS_PATH + player.getHand().get(i).toString();
                    if (player.getName().equals(player2Name))
                        imagePath = CARDS_PATH + "back";
                    image = new Image(imagePath + ".png");
                } else {
                    image = new Image(CARDS_PATH + "empty.png");
                }
                playerCards.get(i).setImage(image);
            }

            //System.out.println(String.format("player %s", String.valueOf(player.getScore())));
            textArea.setText(String.valueOf(player.getScore()));
        });
    }

    /**
     * Update The deck both stock pile and discard pile
     * @param deck The gin rummy deck
     */
    void UpdateDeckUI(Deck deck) {
        Platform.runLater(() -> {
            Image image = new Image(CARDS_PATH + deck.getTopDiscardPileCard().toString() + ".png");
            deckCards.get(1).setImage(image);
        });
    }

    /**
     * Creates a JavaFX scene for the game play interface.
     *
     * @return A JavaFX Scene object representing the game play interface.
     */
    public Scene createGamePlayScene() {

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        // Create player labels
        player1Label = new Label("Player 1: " + player1Name);
        player2Label = new Label("Player 2: " + player2Name);

        Button knockButton = new Button("Try Knock");
        knockButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleKnock();
            }
        });

        Button saveButton = new Button("Save Data");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if(serverSocket != null) {
                        XMLLoader.saveGameToXML("gin_rummy_moves.xml", ginRummyGame);
                    }
                    else {
                        XMLLoader.saveGameToXML("gin_rummy_moves2.xml", ginRummyGame);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // Create player card areas
        HBox player1CardsPane = new HBox(10);
        HBox player2CardsPane = new HBox(10);
        player1CardsPane.setPadding(new Insets(10));//Insets class stores the inside offsets for the four sides of the rectangular area.
        player2CardsPane.setPadding(new Insets(10));

        // Draw/Paint player cards
        for (int i = 0; i < PLAYER_HAND_SIZE; i++) {
            ImageView player1Card = createCardImageView("empty.png");
            ImageView player2Card = createCardImageView("empty.png");

            player1Card.setOnMouseClicked(e -> {
                try {
                    handleMouseClicked(e, player1Card);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            player1Cards.add(player1Card);
            player2Cards.add(player2Card);

            player1CardsPane.getChildren().add(player1Cards.get(i));
            player2CardsPane.getChildren().add(player2Card);
        }

        ImageView deckCard = createCardImageView("back.png");
        ImageView deckCard1 = createCardImageView("empty.png");
        deckCards.add(deckCard);
        deckCards.add(deckCard1);

        // Add player labels and card areas to the borderPane layout
        VBox player1Box = new VBox(10, player1Label, player1CardsPane, knockButton, textAreaPlayer1, saveButton);
        VBox player2Box = new VBox(10, player2Label, player2CardsPane);
        HBox player2ComboBox = new HBox(10,player2Box, textAreaPlayer2);
        HBox deckBox = new HBox(25, deckCards.get(0), deckCards.get(1));
        deckBox.setAlignment(Pos.CENTER);

        VBox comboBox = new VBox(25, player1Box, deckBox);

        borderPane.setLeft(comboBox);
        borderPane.setBottom(player2ComboBox);
        borderPane.setStyle("-fx-background-color: green;");

        chatArea(borderPane);

        Scene scene = new Scene(borderPane, 1400, 700);
        scene.setFill(Color.BLACK); // Set the background color to green

        return scene;
    }

    /**
     * Chat Area of the game where the player chat with other player
     * @param borderPane
     */
    private void chatArea(BorderPane borderPane) {
        chatListView = new ListView<>();
        messageField = new TextField();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(event -> {
            try {
                sendMessage();
            } catch (MalformedURLException | NotBoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        HBox inputBox = new HBox(10, messageField, sendButton);
        VBox chatBox = new VBox(10, chatListView, inputBox);

        borderPane.setRight(chatBox);
    }

    /**
     * Creates a JavaFX scene to display the high scores of players and moves of that player.
     *
     * @return A JavaFX Scene object containing a ListView of player names and scores.
     */
    private Scene createHighScoreScene() {
        List<Player> players;
        try {
            players = HighScoresLoader.loadPlayerData();
        } catch (IOException e) {
            players = new ArrayList<>();
        }

        // Create an ObservableList for the ListView
        ObservableList<String> playerListItems = FXCollections.observableArrayList();

        // Populate the ObservableList with player names and scores
        for (Player player : players) {
            StringBuilder playerInfo = new StringBuilder();
            playerInfo.append(player.getName() + " - Score: " + player.getScore() + "\n");
            playerInfo.append(PlayerMovesInfo.getMoves(player));

            playerListItems.add(playerInfo.toString());
        }

        // Create a ListView to display player names and scores
        ListView<String> playerListView = new ListView<>(playerListItems);
        playerListView.setPrefSize(300, 200);

        // Create a VBox to hold the ListView
        VBox vbox = new VBox(playerListView);

        // Create the scene
        return new Scene(vbox, 400, 300);
    }

    private Scene createMovesDisplayScene() {
        // Create an ObservableList for the ListView
        ObservableList<String> playerListItems = FXCollections.observableArrayList();

        // Populate the ObservableList with player names and scores
        StringBuilder playerInfo = new StringBuilder();
        playerInfo.append(PlayerMovesInfo.getMoves(ginRummyGame.player1));

        playerListItems.add(playerInfo.toString());

        // Create a ListView to display player names and scores
        ListView<String> playerListView = new ListView<>(playerListItems);
        playerListView.setPrefSize(300, 200);

        // Create a VBox to hold the ListView
        VBox vbox = new VBox(playerListView);

        // Create the scene
        return new Scene(vbox, 400, 300);
    }

    /**
     * The chat client send message using this
     * @throws MalformedURLException
     * @throws NotBoundException
     * @throws RemoteException
     */
    private void sendMessage() throws MalformedURLException, NotBoundException, RemoteException {
        chatServer.sendMessage(player1Name + ": " + messageField.getText().trim());
    }

    /**
     * Create Card Image View to be displayed on UI
     * @param path
     * @return
     */
    private ImageView createCardImageView(String path) {
        ImageView card = new ImageView(CARDS_PATH + path);
        card.setFitWidth(CARD_WIDTH);
        card.setFitHeight(CARD_HEIGHT);

        card.setPickOnBounds(true); // allows click on transparent areas
        return card;
    }

    /**
     * Handle Mouse clicks of the player hand card displayed on UI
     * @param event
     * @param source
     * @throws IOException
     */
    private void handleMouseClicked(MouseEvent event, ImageView source) throws IOException {
        int sourceIndex = player1Cards.indexOf(source);
        if(sourceIndex < ginRummyGame.player1.getHand().size() && ginRummyGame.player1.getHand().get(sourceIndex).isDiscardable) {
            ginRummyGame.player1.addToDiscardPile(ginRummyGame.player1.getHand().get(sourceIndex),
                    ginRummyGame.deck);

            ginRummyGame.player1.drawFromStockPile(ginRummyGame.deck);
            ginRummyGame.UpdateCardDiscardableStatus();
        }

        sendDataTransfer();
        UpdateThePlayerHandUI(ginRummyGame.player1, player1Cards, textAreaPlayer1);
        UpdateThePlayerHandUI(ginRummyGame.player2, player2Cards, textAreaPlayer2);
        UpdateDeckUI(ginRummyGame.deck);

        sendDataString("Start");

        event.consume();
    }

    /**
     * If the player press the knock button this method will be called to check if knock possible or not by
     * showing alert window
     */
    private void handleKnock() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Try Knock");

        if(ginRummyGame.canKnock()) {
            ginRummyGame.updateScore();
            try {
                sendDataTransfer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            alert.setContentText("knocked!!!");
        } else {
            alert.setContentText("Cannot knock");
        }
        alert.showAndWait();

        UpdateThePlayerHandUI(ginRummyGame.player1, player1Cards, textAreaPlayer1);
        UpdateThePlayerHandUI(ginRummyGame.player2, player2Cards, textAreaPlayer2);
        try {
            sendDataTransfer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The thread on which first client will listen to other client it may have to use server docket to listen
     * and accept the request
     * @throws IOException
     */
    void communicateWithPeerWithServerThread() throws IOException {
        ObjectInputStream inputStream = null;

        try {
            serverSocket = new ServerSocket(9000);//ServerSocket to listen for incoming connections
            System.out.println("Server is running and waiting for connections on port " + 9000);

            // Accept an incoming connection
            Socket client2Socket = serverSocket.accept();
            System.out.println("Connection established with Other Player");

            // Create input and output streams for communication
            outputStream = new ObjectOutputStream(client2Socket.getOutputStream());
            inputStream = new ObjectInputStream(client2Socket.getInputStream());
            Object peerMessage;

            if(!loadedGame) {
                peerMessage = inputStream.readObject();
                player2Name = (String) peerMessage;

                runGame();
                ginRummyGame.player2.setName(player2Name);
                Platform.runLater(() -> {
                    player2Label.setText("Player2: " + ginRummyGame.player2.getName());
                });
                sendDataTransfer();
            } else {
                ginRummyGame = XMLLoader.loadGameFromXML("gin_rummy_moves.xml");
                sendDataTransfer();
                UpdateThePlayerHandUI(ginRummyGame.player1, player1Cards, textAreaPlayer1);
                //UpdateThePlayerHandUI(ginRummyGame.player2, player2Cards, textAreaPlayer2);
                Platform.runLater(() -> {
                    player1Label.setText("Player1: " + ginRummyGame.player1.getName());
                    player2Label.setText("Player2: " + ginRummyGame.player2.getName());
                });
                UpdateDeckUI(ginRummyGame.deck);
            }

            while(!ginRummyGame.getIsGameOver()) {
                // Receive and display messages from the peer
                peerMessage = inputStream.readObject();

                if(peerMessage instanceof DataTransfer) {
                    DataTransfer dataTransfer = (DataTransfer) peerMessage;
                    ginRummyGame.setPlayer2(dataTransfer.player1);
                    ginRummyGame.player2.setScore(dataTransfer.score);
                    ginRummyGame.player2.setName(dataTransfer.name);
                    player2Name = dataTransfer.name;
                    ginRummyGame.setDeck(dataTransfer.deck);
                    System.out.println(dataTransfer.name);
                    if(!ginRummyGame.isGameOver)
                        ginRummyGame.setIsGameOver(dataTransfer.isGameOver);
                    if(loadedGame) {
                        Platform.runLater(() -> {
                            player1Label.setText("Player1: " + ginRummyGame.player1.getName());
                            player2Label.setText("Player2: " + ginRummyGame.player2.getName());
                        });
                    }
                }

                UpdateThePlayerHandUI(ginRummyGame.player1, player1Cards, textAreaPlayer1);
                UpdateThePlayerHandUI(ginRummyGame.player2, player2Cards, textAreaPlayer2);
                UpdateDeckUI(ginRummyGame.deck);

                ginRummyGame.getIsGameOver();
                ginRummyGame.UpdateCardDiscardableStatus();
                System.out.println("Running");
                Thread.sleep(50);
            }

            ginRummyGame.updateScore();
            HighScoresLoader.savePlayerData(ginRummyGame.player1);
            switchToMovesDisplayScene();

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            if(e.getMessage() != null && e.getMessage().contains("Address already in use")){
                communicateWithPeerThread();
            }
        } finally {
            if(outputStream != null) outputStream.close();
            if(inputStream != null) inputStream.close();
            if(serverSocket != null) serverSocket.close();
            chatServer.unregisterClient(chatClient);
        }
    }

    /**
     * The thread on which other player listen and respond to previous one
     * @throws IOException
     */
    void communicateWithPeerThread() throws IOException {
        ObjectInputStream inputStream = null;

        try {
            clientSocket = new Socket("localhost", 9000);

            // Create input and output streams for communication
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            Object peerMessage;

            if(!loadedGame) {
                sendDataString(player1Name);

                peerMessage = inputStream.readObject();
                if (peerMessage instanceof DataTransfer) {
                    DataTransfer dataTransfer = (DataTransfer) peerMessage;
                    runGame(dataTransfer.deck, dataTransfer.player2, dataTransfer.player1);
                    player2Name = ginRummyGame.player2.getName();
                    Platform.runLater(() -> {
                        player2Label.setText("Player2: " + player2Name);
                    });
                    UpdateThePlayerHandUI(ginRummyGame.player2, player2Cards, textAreaPlayer2);
                    UpdateDeckUI(dataTransfer.deck);
                }

                System.out.println(ginRummyGame.deck.getStockPile().size());
                sendDataTransfer();
            } else {
                ginRummyGame = XMLLoader.loadGameFromXML("gin_rummy_moves2.xml");
                sendDataTransfer();
                UpdateThePlayerHandUI(ginRummyGame.player1, player1Cards, textAreaPlayer1);
                //UpdateThePlayerHandUI(ginRummyGame.player2, player2Cards, textAreaPlayer2);
                Platform.runLater(() -> {
                    player1Label.setText("Player1: " + ginRummyGame.player1.getName());
                    player2Label.setText("Player2: " + ginRummyGame.player2.getName());
                });
                UpdateDeckUI(ginRummyGame.deck);
            }

            while(!ginRummyGame.getIsGameOver()) {

                peerMessage = inputStream.readObject();

                if(peerMessage instanceof DataTransfer) {
                    DataTransfer dataTransfer = (DataTransfer) peerMessage;
                    ginRummyGame.setPlayer2(dataTransfer.player1);
                    ginRummyGame.setDeck(dataTransfer.deck);
                    ginRummyGame.player2.setScore(dataTransfer.score);
                    ginRummyGame.player2.setName(dataTransfer.name);
                    player2Name = dataTransfer.name;
                    if(!ginRummyGame.isGameOver)
                        ginRummyGame.setIsGameOver(dataTransfer.isGameOver);
                    if(loadedGame) {
                        Platform.runLater(() -> {
                            player1Label.setText("Player1: " + ginRummyGame.player1.getName());
                            player2Label.setText("Player2: " + ginRummyGame.player2.getName());
                        });
                    }
                }

                UpdateThePlayerHandUI(ginRummyGame.player1, player1Cards, textAreaPlayer1);
                UpdateThePlayerHandUI(ginRummyGame.player2, player2Cards, textAreaPlayer2);
                UpdateDeckUI(ginRummyGame.deck);

                System.out.println("Running");
                ginRummyGame.getIsGameOver();
                ginRummyGame.UpdateCardDiscardableStatus();
                Thread.sleep(50);
            }

            ginRummyGame.updateScore();
            HighScoresLoader.savePlayerData(ginRummyGame.player1);
            switchToMovesDisplayScene();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if(outputStream != null) outputStream.close();
            if(inputStream != null) inputStream.close();
            if(clientSocket != null) clientSocket.close();
            chatServer.unregisterClient(chatClient);
        }
    }

    /**
     * If user presses the close button the alert pop-up to ask the player whether he wants to save the game or not
     * @param event
     */
    private void handleOnClose(WindowEvent event){
        event.consume(); // Consume the event to prevent the window from closing immediately

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Confirmation");
        alert.setHeaderText("Do you want to save your changes?");
        alert.setContentText("Choose your option:");

        ButtonType buttonSave = new ButtonType("Save");
        ButtonType buttonDontSave = new ButtonType("Don't Save");
        ButtonType buttonCancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(buttonSave, buttonDontSave, buttonCancel);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonSave) {
                System.out.println("Save confirmed.");
                try {
                    if(serverSocket != null)
                        XMLLoader.saveGameToXML("gin_rummy_moves.xml", ginRummyGame);
                    else
                        XMLLoader.saveGameToXML("gin_rummy_moves2.xml", ginRummyGame);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                primaryStage.close();
                Platform.exit();
                System.exit(0);
            } else if (response == buttonDontSave) {
                System.out.println("Don't save confirmed.");
                primaryStage.close();
                Platform.exit();
                System.exit(0);
            } else {
                // User canceled the close action
                // Do nothing
            }
        });
    }

    /**
     * Send the data to other player, DataTransfer Object
     * @throws IOException
     */
    private void sendDataTransfer() throws IOException {
        if(outputStream != null) {
            //System.out.println("AAAAAA: "+ ginRummyGame.player1.getScore());
            DataTransfer dataTransfer = new DataTransfer(ginRummyGame.player1, ginRummyGame.player2,
                    ginRummyGame.deck, ginRummyGame.isGameOver, ginRummyGame.player1.getScore(),
                    ginRummyGame.player1.getName());
            //System.out.println("LhhhL: "+ dataTransfer.player1.getScore());
            outputStream.writeObject(dataTransfer);
            outputStream.flush();
        }
    }

    /**
     * Send the data to other player, but it will be string
     * @param out
     * @throws IOException
     */
    private void sendDataString(String out) throws IOException {
        if(outputStream != null) {
            outputStream.writeObject(out);
            outputStream.flush();
        }
    }

    /**
     * To run the game
     */
    private void runGame() {//first tome game run
        ginRummyGame = new GinRummyGame(player1Name, player2Name);
        ginRummyGame.play();
        Card card = ginRummyGame.deck.drawFromStockPile();
        ginRummyGame.deck.addCardToDiscardPile(card);
        UpdateThePlayerHandUI(ginRummyGame.player1, player1Cards, textAreaPlayer1);
    }

    /**
     * run game with params
     * @param deck
     * @param player1
     * @param player2
     */
    private void runGame(Deck deck, Player player1, Player player2) {
        ginRummyGame = new GinRummyGame(deck, player1, player2);
        ginRummyGame.play();
        UpdateThePlayerHandUI(ginRummyGame.player1, player1Cards, textAreaPlayer1);
    }

    /**
     * Switching the scene to Game Play Scene
     */
    private void switchToGameplayScene() {
        Platform.runLater(() -> {
            gameplayScene = createGamePlayScene();
            primaryStage.setScene(gameplayScene);
        });
    }

    /**
     * Switching the scene to Player Name Selection Scene
     */
    private void switchToPlayerNameScene() {
        primaryStage.setScene(playerNameScene);
    }

    /**
     * Switching the scene to High Score Display Scene
     */
    private void switchToHighScoreScene() {
        Platform.runLater(() -> {
            highScoreScene = createHighScoreScene();
            primaryStage.setScene(highScoreScene);
        });
    }

    /**
     * Switching the scene to High Score Display Scene
     */
    private void switchToMovesDisplayScene() {
        Platform.runLater(() -> {
            movesDisplayScene = createMovesDisplayScene();
            primaryStage.setScene(movesDisplayScene);
        });
    }

    /**
     * The main method to run this game
     * @param args
     */
    public static void main(String[] args) {
        launch();//starts the JavaFX application
    }
}