package com.gameserver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.io.File;
import java.io.FileOutputStream;

import com.gameserver.Paddle.Side;

public class GameData extends Thread{
    private static final short GAME_STATE_SIZE = 151;
    private static final short NAME_SIZE = 30;
    private static final short SREVER_MSSG_SIZE = 60;
    public ClientHandle[] gameClients = new ClientHandle[2];
    public Ball ball = new Ball();
    public Paddle paddle1 = new Paddle(Side.LEFT);
    public Paddle paddle2 = new Paddle(Side.RIGHT);
    public int player1Score = 0;
    public int player2Score = 0;
    private String globalMssg = "";
    private int messageTimer = 0;
    private boolean timerActive = true;

    private void setMessage(String message){
        this.globalMssg = message;
        messageTimer = 5;
    }

    public GameData() {
        this.start();
    }

    public void clientJoin(byte[] clientData, InetAddress clientAddr, int clientPort) {
        //Deserialize the data
        PlayerState ps = new PlayerState(clientData);

        //Create a new client handle and update game data
        gameClients[clientData[0] - 1] = new ClientHandle(clientAddr, clientPort, ps.playerIdentifier, ps.playerName, ps.playerColor);
        if(ps.playerIdentifier == (byte)1)
            paddle1.movePaddle(ps.paddlePosY);
        else
            paddle2.movePaddle(ps.paddlePosY);
        setMessage(String.format("Player %s has connected.", ps.playerName));
        
        //Update server info
        GameServer.serverInfo.player1Name = ps.playerIdentifier == (byte)1 ? ps.playerName : GameServer.serverInfo.player1Name;
        GameServer.serverInfo.player2Name = ps.playerIdentifier == (byte)2 ? ps.playerName : GameServer.serverInfo.player2Name;
        GameServer.serverInfo.player1Color = ps.playerIdentifier == (byte)1 ? ps.playerColor : GameServer.serverInfo.player1Color;
        GameServer.serverInfo.player2Color = ps.playerIdentifier == (byte)2 ? ps.playerColor : GameServer.serverInfo.player2Color;
        GameServer.serverInfo.isFull = playersInSession() == gameClients.length;
        GameServer.hub.sendModify();
    }

    public void clientUpdate(byte[] clientData){
        //Deserialize playerstate
        PlayerState ps = new PlayerState(clientData);
        if(ps.playerIdentifier == (byte)1)
            paddle1.movePaddle(ps.paddlePosY);
        else
            paddle2.movePaddle(ps.paddlePosY);

        gameClients[ps.playerIdentifier - 1].resetTimeout();
    }

    public void clientDisconnect(byte playerIdentifier){
        //Get the disconnected player's instance
        ClientHandle disconnectedClient = gameClients[playerIdentifier - 1];
        //Update game data
        gameClients[playerIdentifier - 1] = null;

        if(disconnectedClient.playerIdentifier == (byte)1){
            if(gameClients[1] != null)
                addScoreToFile(String.format("%s's Score: %d %s's Score: %d\n", disconnectedClient.getName(), player1Score, gameClients[1].playerName, player2Score));
            paddle1.movePaddle(240);
        }
            
        if(disconnectedClient.playerIdentifier == (byte)2){
            if(gameClients[0] != null)
                addScoreToFile(String.format("%s's Score: %d %s's Score: %d\n", gameClients[0].playerName, player1Score, disconnectedClient.getName(), player2Score));
            paddle2.movePaddle(240);
        }
        
        player1Score = 0;
        player2Score = 0;

        setMessage(String.format("Player %s has disconnected.", disconnectedClient.playerName));

        //Update server info
        GameServer.serverInfo.player1Name = disconnectedClient.playerIdentifier == (byte)1 ? "" : GameServer.serverInfo.player1Name;
        GameServer.serverInfo.player2Name = disconnectedClient.playerIdentifier == (byte)2 ? "" : GameServer.serverInfo.player2Name;
        GameServer.serverInfo.player1Color = disconnectedClient.playerIdentifier == (byte)1 ? new byte[] {(byte)0, (byte)0, (byte)0} : GameServer.serverInfo.player1Color;
        GameServer.serverInfo.player2Color = disconnectedClient.playerIdentifier == (byte)2 ? new byte[] {(byte)0, (byte)0, (byte)0} : GameServer.serverInfo.player2Color;

        

        GameServer.serverInfo.isFull = playersInSession() == gameClients.length;
        GameServer.hub.sendModify();
    }

    public byte[] serializeCurrentState(byte playerIdentifier){
        byte[] data = new byte[GAME_STATE_SIZE];
        int counter = 0;

        //Serialize playerIdentifier
        data[0] = playerIdentifier;
        counter += 1;

        //Serialize p1 name
        byte[] bytes = gameClients[0] == null ? "Waiting For Player...".getBytes(StandardCharsets.US_ASCII) : gameClients[0].playerName.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < NAME_SIZE; i++){
            if(i < bytes.length)
                data[counter + i] = bytes[i];
            else
                data[counter + i] = 0;
        }
        counter += NAME_SIZE;

        //Serialize p2 name
        bytes = gameClients[1] == null ? "Waiting For Player...".getBytes(StandardCharsets.US_ASCII) : gameClients[1].playerName.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < NAME_SIZE; i++){
            if(i < bytes.length)
                data[counter + i] = bytes[i];
            else
                data[counter + i] = 0;
        }
        counter += NAME_SIZE;

        //Serialize p1 score
        bytes = ByteBuffer.allocate(4).putInt(player1Score).array();
        for(int i = 0; i < bytes.length; i++){
            data[counter + i] = bytes[i];
        }
        counter += 4;

        //Serialize p2 score
        bytes = ByteBuffer.allocate(4).putInt(player2Score).array();
        for(int i = 0; i < bytes.length; i++){
            data[counter + i] = bytes[i];
        }
        counter += 4;

        //Serialize p1 color
        bytes = gameClients[0] == null ? new byte[]{(byte)255,(byte)255,(byte)255} : gameClients[0].playerColor;
        for(int i = 0; i < bytes.length; i++){
            data[counter + i] = bytes[i];
        }
        counter += 3;

        //Serialize p2 color
        bytes = gameClients[1] == null ? new byte[]{(byte)255,(byte)255,(byte)255} : gameClients[1].playerColor;
        for(int i = 0; i < bytes.length; i++){
            data[counter + i] = bytes[i];
        }
        counter += 3;

        //Serialize p1 paddle height
        bytes = ByteBuffer.allocate(4).putFloat(paddle1.center.y).array();
        for(int i = 0; i < bytes.length; i++){
            data[counter + i] = bytes[i];
        }
        counter += 4;

        //Serialize p2 paddle height
        bytes = ByteBuffer.allocate(4).putFloat(paddle2.center.y).array();
        for(int i = 0; i < bytes.length; i++){
            data[counter + i] = bytes[i];
        }
        counter += 4;

        //Serialize ball x pos
        bytes = ByteBuffer.allocate(4).putFloat(ball.position.x).array();
        for(int i = 0; i < bytes.length; i++){
            data[counter + i] = bytes[i];
        }
        counter += 4;

        //Serialize ball y pos
        bytes = ByteBuffer.allocate(4).putFloat(ball.position.y).array();
        for(int i = 0; i < bytes.length; i++){
            data[counter + i] = bytes[i];
        }
        counter += 4;

        //Serialzie server message
        bytes = globalMssg.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < SREVER_MSSG_SIZE; i++){
            if(i < bytes.length)
                data[counter + i] = bytes[i];
            else
                data[counter + i] = 0;
        }

        return data;
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

    public void addScoreToFile(String s){
        try {
            File scores = new File("score.txt");
            scores.createNewFile();
            FileOutputStream fos = new FileOutputStream(scores, true);
            fos.write(s.getBytes(StandardCharsets.US_ASCII));
            fos.close();
        } catch (Exception e) {
            System.out.println("Couldnt write to file");
        }
    }

    /**
     * Start a thread that holds messages up for a certian period of time, then clears them.
     */
    @Override
    public void run(){
        while(timerActive){
            try {
                if(messageTimer == 0){
                    this.globalMssg = "";
                }

                Thread.sleep(1000);
                if(messageTimer > 0)
                    messageTimer--;
            } catch (Exception e) {
                this.globalMssg = "";
            }
        }
    }
}
