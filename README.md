# GinRummy
Fully functional Gin Rummy game made for college project.

These are all functionalities that are implemented in this project.

JavaFX: functionalities for playing the game, where it is necessary to implement the first screen for selecting the player's name, the second screen for playing the game, the third screen for printing the most successful players (who defeated the opponent the fastest) and the screen for printing all the player's moves.

Serialization and reflection: "SAVE" and "LOAD" functionality so that the game can continue. With reflection, it is necessary to implement the automatic generation of documentation for the program code.

Network communication: enabled online game play on multiple separate screens using "sockets", "chat" functionality linked using RMI, and reading network settings from the system file using "JNDI" technology.

Threads: screen refresh with new game moves, synchronize file access in terms of reading and writing data about played moves in the game.

XML: functionality of saving data in a file in XML format so that they can be read and simulate the "Replay" functionality of replaying an already played game.


Game can be easily started by firstly starting ChatServer for communication between players and GinUI1 and GinUI2 after that for name and game screens.

