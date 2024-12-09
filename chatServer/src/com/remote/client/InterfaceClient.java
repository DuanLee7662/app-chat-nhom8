package com.remote.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface InterfaceClient extends Remote {

    String getName() throws RemoteException;

    void retrieveMessage(String message) throws RemoteException;

    void retrieveMessage(String filename, ArrayList<Integer> inc) throws RemoteException;

    void retrieveMessageAdd(String message) throws RemoteException;

    void closeChat(String message) throws RemoteException;

    void openChat() throws RemoteException;

    void retrieveMessageRemove(String message) throws RemoteException; // Added method

}
