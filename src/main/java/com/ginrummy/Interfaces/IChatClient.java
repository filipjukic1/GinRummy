package com.ginrummy.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The IChatClient interface defines the methods that a remote chat client can invoke
 * when interacting with a chat server through RMI (Remote Method Invocation).
 * This interface extends the Remote interface to mark it as remotely accessible.
 */
public interface IChatClient extends Remote {

    /**
     * Receives a message from the chat server.
     *
     * @param message The message received from the chat server.
     * @throws RemoteException If a communication error occurs during the remote method call.
     */
    abstract void receiveMessage(String message) throws RemoteException;
}