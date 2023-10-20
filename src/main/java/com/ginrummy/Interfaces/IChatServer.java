package com.ginrummy.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The IChatServer interface defines the methods that a remote chat server can provide
 * to manage chat messages and client registrations through RMI (Remote Method Invocation).
 * This interface extends the Remote interface to mark it as remotely accessible.
 */
public interface IChatServer extends Remote {

    /**
     * Sends a message to all connected chat clients.
     *
     * @param message The message to be broadcasted to all chat clients.
     * @throws RemoteException If a communication error occurs during the remote method call.
     */
    void sendMessage(String message) throws RemoteException;

    /**
     * Registers a chat client with the chat server.
     *
     * @param client The chat client to be registered.
     * @throws RemoteException If a communication error occurs during the remote method call.
     */
    void registerClient(IChatClient client) throws RemoteException;

    /**
     * Unregisters a chat client from the chat server.
     *
     * @param client The chat client to be unregistered.
     * @throws RemoteException If a communication error occurs during the remote method call.
     */
    void unregisterClient(IChatClient client) throws RemoteException;
}