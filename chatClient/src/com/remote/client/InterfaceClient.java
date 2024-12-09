package com.remote.client;

import java.util.ArrayList;
import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;



public interface InterfaceClient extends Remote {

    void retrieveMessage(String message) throws RemoteException;

    void retrieveMessageAdd(String message) throws RemoteException;

    void retrieveMessageRemove(String message) throws RemoteException;

    void retrieveMessage(String filename, ArrayList<Integer> inc) throws RemoteException;

    void sendMessage(List<String> list) throws RemoteException;

    void sendRemoveMessage() throws RemoteException;

    String getName() throws RemoteException;

    void closeChat(String message) throws RemoteException;

    void openChat() throws RemoteException;

}
