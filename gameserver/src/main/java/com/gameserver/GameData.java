package com.gameserver;

import java.net.InetAddress;

public class GameData extends Thread{
    private static final short MAX_MESSAGE_SIZE = 50;

    public ClientHandle[] gameClients = new ClientHandle[2];
    public Vector2 ballVelDir = new Vector2();
    public Vector2 p2VecPos = new Vector2();
    public Vector2 ballVecPos = new Vector2();
    public int p1score = 0;
    public int p2score = 0;
    private String globalMssg;

    private void setMessage(String message){
        this.globalMssg = message;
        this.interrupt();
        this.start();
    }

    public GameData() {
    }

    public void clientJoin(byte[] clientData, InetAddress clientAddr, int clientPort) {
        //Deserialize the data
        PlayerState ps = new PlayerState(clientData);

        //Create a new client handle and update game data
        gameClients[clientData[0] - 1] = new ClientHandle(clientAddr, clientPort, ps.playerIdentifier, ps.playerName, ps.playerColor);
        //TODO: UPDATE PADDLE POSITION
        setMessage(String.format("Player %s has connected.", ps.playerName));
        

        //Update server info
        GameServer.serverInfo.player1Name = ps.playerIdentifier == (byte)1 ? ps.playerName : GameServer.serverInfo.player1Name;
        GameServer.serverInfo.player2Name = ps.playerIdentifier == (byte)2 ? ps.playerName : GameServer.serverInfo.player2Name;
        GameServer.serverInfo.isFull = playersInSession() == gameClients.length;
        GameServer.hub.sendModify();
    }

    public void clientUpdate(byte[] clientData){
        //Deserialize playerstate
        PlayerState ps = new PlayerState(clientData);
        //TODO: UDPATE PADDLE POSITION

        gameClients[ps.playerIdentifier - 1].resetTimeout();
    }

    public void clientDisconnect(byte playerIdentifier){
        //Get the disconnected player's instance
        ClientHandle disconnectedClient = gameClients[playerIdentifier - 1];
        //Update game data
        gameClients[playerIdentifier - 1] = null;
        p1score = 0;
        p2score = 0;
        setMessage(String.format("Player %s has disconnected.", disconnectedClient.playerName));

        //Update server info
        GameServer.serverInfo.player1Name = disconnectedClient.playerIdentifier == (byte)1 ? "" : GameServer.serverInfo.player1Name;
        GameServer.serverInfo.player2Name = disconnectedClient.playerIdentifier == (byte)2 ? "" : GameServer.serverInfo.player2Name;
        GameServer.serverInfo.isFull = playersInSession() == gameClients.length;
        GameServer.hub.sendModify();

    }

    public byte[] serializeCurrentState(byte playerIdentifier){
        //TODO
        return null;
    }

    public boolean playerExists(byte playerIdentifier){
        return gameClients[playerIdentifier - 1] != null;
    }

    public int playersInSession() {
        int count = 0;
        for(ClientHandle ch : gameClients){
            if(ch != null)
                count++;
        }

        return count;
    }

    public int getOpenPlayerSlot(){
        int count = 0;
        for(ClientHandle ch : gameClients){
            if(ch == null)
                return count;
            count++;
        }

        return -1;
    }

    /**
     * Start a thread that holds messages up for a certian period of time, then clears them.
     */
    @Override
    public void run(){
        try {
            Thread.sleep(5000);
            this.globalMssg = "";
        } catch (Exception e) {
            this.globalMssg = "";
        }
    }
}
