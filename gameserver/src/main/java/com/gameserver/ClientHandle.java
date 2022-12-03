package com.gameserver;

import java.net.*;

public class ClientHandle extends Thread {
    private static final short TIMEOUT_DURATION = 10;
    
    private boolean isActive;
    private short currentTimeoutCount;

    public InetAddress clientAddr;
    public int clientPort;
    public byte playerIdentifier;
    public  String playerName = "";
    public byte[] playerColor = {(byte)255, (byte)255, (byte)255};


    public ClientHandle(InetAddress clientAddr, int clientPort, byte playerIdentifier, String playerName, byte[] playerColor){
        //Initialize the handle
        this.clientAddr = clientAddr;
        this.clientPort = clientPort;
        this.playerIdentifier = playerIdentifier;
        this.playerName = playerName;
        this.playerColor = playerColor;
        this.isActive = true;
        this.currentTimeoutCount = TIMEOUT_DURATION;
        
        //Start timeout loop
        this.start();
    }

    
    public void endHandle(){
        this.isActive = false;
        GameServer.gameData.clientDisconnect(playerIdentifier);
    }

    public void resetTimeout(){
        this.currentTimeoutCount = TIMEOUT_DURATION;
    }

    @Override
    public void run() {
        while(isActive){
            try {
                if(currentTimeoutCount == 0){
                    endHandle();
                    return;
                }
    
                Thread.sleep(1000);
                currentTimeoutCount--;

            } catch (InterruptedException e) {
                endHandle();
                return;
            }
        }
    }
}
