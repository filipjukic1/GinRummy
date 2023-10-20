package com.ginrummy.ChatFeatureRMI;

import com.ginrummy.Interfaces.IChatClient;
import com.ginrummy.Interfaces.IChatServer;
import com.ginrummy.NetworkSettingsJNDI.NetworkSettings;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * The ChatServer class represents a remote chat server that allows clients to register, unregister,
 * and exchange messages in a chat application.
 */
public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private final List<IChatClient> clients = new ArrayList<>();
    public static String serverIP = "localhost";

    /**
     * Default constructor for the ChatServer class.
     *
     * @throws RemoteException If there is an issue with remote communication.
     */
    public ChatServer() throws RemoteException {
    }

    /**
     * Registers a client with the chat server.
     *
     * @param client The client to be registered.
     * @throws RemoteException If there is an issue with remote communication.
     */
    @Override
    public void registerClient(IChatClient client) throws RemoteException {
        clients.add(client);
    }

    /**
     * Unregisters a client from the chat server.
     *
     * @param client The client to be unregistered.
     * @throws RemoteException If there is an issue with remote communication.
     */
    @Override
    public void unregisterClient(IChatClient client) throws RemoteException {
        clients.remove(client);
    }

    /**
     * Sends a message to all registered clients.
     *
     * @param message The message to be sent.
     * @throws RemoteException If there is an issue with remote communication.
     */
    @Override
    public void sendMessage(String message) throws RemoteException {
        for (IChatClient client : clients) {
            client.receiveMessage(message);
        }
    }

    /**
     * Retrieves an instance of the IChatServer from the RMI registry.
     *
     * @return An instance of the IChatServer.
     * @throws MalformedURLException If there is an issue with the URL.
     * @throws NotBoundException    If the specified name is not bound in the registry.
     * @throws RemoteException      If there is an issue with remote communication.
     */
    public static IChatServer getIChatServer() throws MalformedURLException, NotBoundException, RemoteException {
        return (IChatServer) Naming.lookup("rmi://" + serverIP + "/ChatServer");
    }

    /**
     * The main method to start the ChatServer.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        try {
            serverIP = NetworkSettings.getIPv4();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("ChatServer", new ChatServer());
            System.out.println("Chat Server is ready and running.....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
