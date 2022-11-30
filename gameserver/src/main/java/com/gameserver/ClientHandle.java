package com.gameserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

//TODO: Jordan
public class ClientHandle extends Thread {

    DatagramSocket sock;
    GameData gameData;

    public ClientHandle(DatagramSocket sock, DatagramPacket rcvPacket, GameData gameData){
        this.sock = sock;
        this.gameData = gameData;
        
        // TODO get player name from rcvPacket
        gameData.addClient(this, "");
    }

    

    @Override
    public void run() {


    }
}
