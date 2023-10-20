package com.ginrummy.ChatFeatureRMI;

import com.ginrummy.Interfaces.IChatClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This abstract class represents a ChatClient, which is a remote object that can participate in a chat application.
 * It implements the IChatClient interface to provide basic chat functionality.
 */
public abstract class ChatClient extends UnicastRemoteObject implements IChatClient {
    private String name;

    /**
     * Constructor for initializing a ChatClient with a given name.
     *
     * @param name The name of the ChatClient.
     * @throws RemoteException If there is an issue with remote communication.
     */
    public ChatClient(String name) throws RemoteException {
        this.name = name;
    }
}