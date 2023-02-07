package com.remote.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import com.remote.client.InterfaceClient;
import java.util.List;
import java.util.Vector;


public class ChatServer extends UnicastRemoteObject implements InterfaceServer{
    private final ArrayList<InterfaceClient> clients; //liste contient tous les clients mais qui ne sont pas bloqué 
    private final ArrayList<InterfaceClient> blockedClients; //liste contient tous les clients bloqués
    
    //constructeur
    public ChatServer() throws RemoteException{
        super();
        this.clients = new ArrayList<>();
        blockedClients = new ArrayList<>();
    }
    
    //cette fonction pour distribuer le message vers tous les clients connectes, ou une a liste presicé par le client (disscution privée)
    @Override
    public synchronized void broadcastMessage(String message,List<String> list) throws RemoteException {
        if(list.isEmpty()){
            int i= 0;
            while (i < clients.size()){
                clients.get(i++).retrieveMessage(message);
            }
        }else{
            for (InterfaceClient client : clients) {
                for(int i=0;i<list.size();i++){
                    if(client.getName().equals(list.get(i))){
                        client.retrieveMessage(message);
                    }
                }
            }
        }
    }
    
    //cette fonction pour distribuer un fichier vers tous les clients connectes, ou une a liste presicé par le client (disscution privée)
    @Override
    public synchronized void broadcastMessage(ArrayList<Integer> inc, List<String> list,String filename) throws RemoteException {
        if(list.isEmpty()){
            int i= 0;
            while (i < clients.size()){
                clients.get(i++).retrieveMessage(filename,inc);
            }
        }else{
            for (InterfaceClient client : clients) {
                for(int i=0;i<list.size();i++){
                    if(client.getName().equals(list.get(i))){
                        client.retrieveMessage(filename,inc);
                    }
                }
            }
        }
    }
        
    //cette fonction pour ajouter un client connectes a la liste des clients sur le serveur
    @Override
    public synchronized void addClient(InterfaceClient client) throws RemoteException {
        this.clients.add(client);
    }
    
    //cette fonction pour recupere le nom des clients connectes
    @Override
    public synchronized Vector<String> getListClientByName(String name) throws RemoteException {
        Vector<String> list = new Vector<>();
        for (InterfaceClient client : clients) {
            if(!client.getName().equals(name)){
                list.add(client.getName());
            }
        }
        return list;
    }
    
    
