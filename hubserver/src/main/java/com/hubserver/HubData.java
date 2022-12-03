package com.hubserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import com.hubserver.ClientHandle.MessageType;

public class HubData {
    private static final int MAX_GAME_SEVERS = 100;
    private LinkedList<ClientHandle> gameClients;
    private LinkedList<ClientHandle> gameServers;
    private HashMap<Integer, ServerInfo> serverList;
    private HashSet<Integer> generatedIdentifiers;
    private int serverPort;

    public HubData(int port){
        gameClients = new LinkedList<ClientHandle>();
        gameServers = new LinkedList<ClientHandle>();
        serverList = new HashMap<Integer, ServerInfo>();
        generatedIdentifiers = new HashSet<Integer>();
        serverPort = port;
    }

    public int generateIdentifier(){
        int ID = -1;
        
        //Find a unique identifier if there is there are any left unused
        while (generatedIdentifiers.size() < MAX_GAME_SEVERS){
            ID = (int)((Math.random() * (MAX_GAME_SEVERS)) + 1);
            
            //When an unused identifier is found, store it and send it out
            if(!generatedIdentifiers.contains(ID)){
                generatedIdentifiers.add(ID);
                break;
            }
        }

        return ID;
    }

    public void notifyGameClientsAdd(int serverIdentifier, byte[] serverInfoData){
        //Deserialize server info data
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.deserialize(serverInfoData);

        //Add server info data to serer list
        serverList.put(serverIdentifier, serverInfo);

        //Send sever info to clients
        byte[] data = serverInfo.serialize();
        for(ClientHandle ch : gameClients){
            ch.send(data, MessageType.ADD_GAMESERVER);
        }
    }

    public void notifyGameClientsModify(int serverIdentifier, byte[] serverInfoData){
        //Get server info reference and deserialize data into it
        ServerInfo serverInfo = serverList.get(serverIdentifier);
        serverInfo.deserialize(serverInfoData);

        //Send sever info to clients
        byte[] data = serverInfo.serialize();
        for(ClientHandle ch : gameClients){
            ch.send(data, MessageType.MODIFY_GAMESERVER);
        }
    }

    public void notifyGameClientsDrop(int serverIdentifier){
        //Tell all clients which server was dropped from the hub
        for(ClientHandle ch : gameClients){
            ch.informOfDrop(serverIdentifier);
        }
    }

    public void sendServerListToClient(ClientHandle gameClient){
        for(ServerInfo serverInfo : serverList.values()){
            gameClient.send(serverInfo.serialize(), MessageType.SERVER_LIST);
        }
    }

    public void addGameClient(ClientHandle gameClient){
        gameClients.add(gameClient);
    }

    public void addGameServer(ClientHandle gameServer){
        gameServers.add(gameServer);
    }

    public void dropGameClient(ClientHandle gameClient){
        gameClients.remove(gameClient);
    }

    public void dropGameServer(ClientHandle gameServer, int serverIdentifier){
        generatedIdentifiers.remove(serverIdentifier);
        serverList.remove(serverIdentifier);
        gameServers.remove(gameServer);
    }

    public void disconnectAllClients(){
        for(ClientHandle ch : gameClients){
            ch.endHandle();
        }

        for(ClientHandle ch : gameServers){
            ch.endHandle();
        }
    }

    public int getGameServerCount(){
        return gameServers.size();
    }

    public int getGameClientCount(){
        return gameClients.size();
    }

    public int getServerPort(){
        return serverPort;
    }
}
