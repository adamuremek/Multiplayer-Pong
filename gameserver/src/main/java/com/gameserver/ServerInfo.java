package com.gameserver;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerInfo{
    private static final int ADDR_SIZE = 256;
    private static final int NAME_SIZE = 30;
    public String serverName = "";
    public String player1Name = "";
    public String player2Name = "";
    public String serverAddr = "";
    public int serverPort = 0;
    public int identifier = 0;
    public boolean isFull = false;

    public byte[] serialize(){
        byte[] serializedData = new byte[355];
        int counter = 0;

        //Serialize server name
        byte[] bytes = serverName.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < NAME_SIZE; i++){
            if(i < bytes.length)
                serializedData[counter + i] = bytes[i];
            else
                serializedData[counter + i] = 0;
        }
        counter += NAME_SIZE;

        //Serialize player 1 name
        bytes = player1Name.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < NAME_SIZE; i++){
            if(i < bytes.length)
                serializedData[counter + i] = bytes[i];
            else
                serializedData[counter + i] = 0;
        }
        counter += NAME_SIZE;

        //Serialize player 2 name
        bytes = player2Name.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < NAME_SIZE; i++){
            if(i < bytes.length)
                serializedData[counter + i] = bytes[i];
            else
                serializedData[counter + i] = 0;
        }
        counter += NAME_SIZE;

        //Serialize server address
        bytes = serverAddr.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < ADDR_SIZE; i++){
            if(i < bytes.length)
                serializedData[counter + i] = bytes[i];
            else
                serializedData[counter + i] = 0;
        }
        counter += ADDR_SIZE;

        //Serialize server port
        bytes = ByteBuffer.allocate(4).putInt(serverPort).array();
        for(int i = 0; i < 4; i++){
                serializedData[counter + i] = bytes[i];
        }
        counter += 4;

        //Serialize server identifier
        bytes = ByteBuffer.allocate(4).putInt(identifier).array();
        for(int i = 0; i < 4; i++){
                serializedData[counter + i] = bytes[i];
        }
        counter += 4;

        //Seralize full state
        serializedData[counter] = isFull ? (byte)1 : (byte)0;

        return serializedData;
    }

    public void deserialize(byte[] data){
        this.serverName = new String(Arrays.copyOfRange(data, 0, 30), StandardCharsets.US_ASCII);
        this.player1Name = new String(Arrays.copyOfRange(data, 30, 60), StandardCharsets.US_ASCII);
        this.player2Name = new String(Arrays.copyOfRange(data, 60, 90), StandardCharsets.US_ASCII);
        this.serverAddr = new String(Arrays.copyOfRange(data, 90, 346), StandardCharsets.US_ASCII);
        this.serverPort = ByteBuffer.wrap(Arrays.copyOfRange(data, 346, 350)).getInt();
        this.identifier = ByteBuffer.wrap(Arrays.copyOfRange(data, 350, 354)).getInt();
        this.isFull = data[354] == (byte)1 ? true : false;  
    }

    @Override
    public String toString(){
        String out = "-------------------\n";
        out += "Server Info:\n";
        out += String.format("Server Name: %s\n", this.serverName);
        out += String.format("Player 1: %s\n", this.player1Name);
        out += String.format("Player 2: %s\n", this.player2Name);
        out += String.format("Server Address: %s\n", this.serverAddr);
        out += String.format("Server Port: %s\n", this.serverPort);
        out += String.format("Server Identifier: %s\n", this.identifier);
        out += String.format("Is Server Full: %s\n", this.isFull);
        out += "-------------------\n";

        return out;
    }
}




