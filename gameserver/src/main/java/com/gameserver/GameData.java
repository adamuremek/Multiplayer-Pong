package com.gameserver;

import java.net.InetAddress;
import com.gameserver.Paddle.Side;

public class GameData extends Thread{
    public ClientHandle[] gameClients = new ClientHandle[2];
    public Ball ball = new Ball();
    public Paddle p1 = new Paddle(Side.LEFT);
    public Paddle p2 = new Paddle(Side.RIGHT);
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
        if(ps.playerIdentifier == (byte)1)
            p1.movePaddle(ps.paddlePosY);
        else
            p2.movePaddle(ps.paddlePosY);
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
        if(ps.playerIdentifier == (byte)1)
            p1.movePaddle(ps.paddlePosY);
        else
            p2.movePaddle(ps.paddlePosY);

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
