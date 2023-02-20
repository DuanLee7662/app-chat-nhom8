package com.remote.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import com.remote.client.InterfaceClient;
import java.util.List;
import java.util.Vector;


public class ChatServer extends UnicastRemoteObject implements InterfaceServer{
    private final ArrayList<InterfaceClient> clients; //List of connected users
    private final ArrayList<InterfaceClient> blockedClients; //List of blocked users
    
    //constructor
    public ChatServer() throws RemoteException{
        super();
        this.clients = new ArrayList<>();
        blockedClients = new ArrayList<>();
    }
    
   
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
        
     @Override
    public synchronized void broadcastMessage(String message) throws RemoteException {
        int i= 0;
            while (i < clients.size()){
                clients.get(i++).retrieveMessage(message);
        }
    }
    
    @Override
    public synchronized void addClient(InterfaceClient client) throws RemoteException {
        this.clients.add(client);
        for(int j=0;j<this.clients.size();j++){
            this.clients.get(j).retrieveMessageAdd(("[Notify]: " + client.getName() + " join the conversation").toUpperCase());
        }
        System.out.println(client.getName() + " join the conversation");
    }
    
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
    
        @Override
    public synchronized void blockClient(List<String> clients){
        for(int j=0;j<this.clients.size();j++){
            for(int i=0;i<clients.size();i++){
                try {
                    if(this.clients.get(j).getName().equals(clients.get(i))){
                        this.clients.get(j).closeChat(clients + " you are blocked by admin");
                        blockedClients.add(this.clients.get(j));
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }
    
    @Override
    public synchronized void removeClient(List<String> clients_list){
        for(int j=0;j<this.clients.size();j++){
            for(int i=0;i<clients_list.size();i++){
                try {
                    if(this.clients.get(j).getName().equals(clients_list.get(i))){
                        this.clients.get(j).closeChat(clients_list.get(i) + " you are removed from the chat");
                        this.clients.remove(j);
                        int k= 0;
                        while (k < clients.size()){
                            clients.get(k++).retrieveMessageRemove(( "[Notify]: " + clients_list.get(i) + " has left the conversation").toUpperCase());
                        }
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
        System.out.println(clients + " has left the conversation");
    }
    
    
    @Override
    public synchronized void removeClient(String clients){
        for(int j=0;j<this.clients.size();j++){
            try {
                if(this.clients.get(j).getName().equals(clients)){
                    this.clients.remove(j);
                    int k= 0;
                    while (k < this.clients.size()){
                         this.clients.get(k++).retrieveMessageRemove(( "[Notify]: " + clients + " has left the conversation").toUpperCase());
                    }
                }
                
            } catch (RemoteException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        System.out.println(clients + " has left the conversation");
    }

    
    @Override
    public synchronized void reactiveClient(List<String> clients) throws RemoteException {
        for(int j=0;j<this.blockedClients.size();j++){
            for(int i=0;i<clients.size();i++){
                try {
                    if(this.blockedClients.get(j).getName().equals(clients.get(i))){
                        this.blockedClients.get(j).openChat();
                        this.blockedClients.remove(j);
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }
    
    @Override
    public boolean checkUsername(String username) throws RemoteException {
        boolean exist = false;
        for(int i=0;i<clients.size();i++){
            if(clients.get(i).getName().equals(username)){
                exist = true;
            }
        }
        for(int i=0;i<blockedClients.size();i++){
            if(blockedClients.get(i).getName().equals(username)){
                exist = true;
            }
        }
        return exist;
    }
}

