package com.gameserver;

import java.io.IOException;
import java.net.*;


public class ClientListener extends Thread {

    private DatagramSocket sock;
    private boolean isActive;
    byte[] buf = new byte[PlayerState.PLAYER_STATE_BUF_SIZE];

    public ClientListener(int port) throws IOException {
        System.out.println("CONSTRUCT");
        this.sock = new DatagramSocket(port);
        this.isActive = true;
        this.start();
        System.out.println("SCOKET MADe");
    }

    private void attemptHeartbeat(DatagramPacket incomingClient){
        try {
            if(GameServer.gameData.playersInSession() < 2){
                //Generate a byte array with an identifer for the new client
                byte[] currentState = GameServer.gameData.serializeCurrentState((byte)(GameServer.gameData.getOpenPlayerSlot() + 1));
                DatagramPacket heartbeatPckt = new DatagramPacket(currentState, currentState.length, incomingClient.getAddress(), incomingClient.getPort());
                sock.send(heartbeatPckt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HEARTBEAT FAILED");
        }
    }

    public void updateClients() {
        try {
            byte[] data = GameServer.gameData.serializeCurrentState((byte) 1);
            if (GameServer.gameData.gameClients[0] != null){
                DatagramPacket packet = new DatagramPacket(data, data.length, GameServer.gameData.gameClients[0].clientAddr, GameServer.gameData.gameClients[0].clientPort);
                sock.send(packet);
            }

            data[0] = (byte)2;
            if (GameServer.gameData.gameClients[1] != null){
                DatagramPacket packet = new DatagramPacket(data, data.length, GameServer.gameData.gameClients[1].clientAddr, GameServer.gameData.gameClients[1].clientPort);
                sock.send(packet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endListener(){
        this.isActive = false;
        sock.close();
    }

    @Override
    public void run() {

        while (this.isActive){
            try {
                //Block and wait for incoming player data
                DatagramPacket incomingPlayerData = new DatagramPacket(buf, buf.length);
                sock.receive(incomingPlayerData);

                //Get the data
                byte[] data = incomingPlayerData.getData();
                
                //Check the identifier byte to see if the player is new or already in the current session.
                switch(data[0]){
                    case (byte)0:
                        //Send the new client a packet with an identifier.
                        //If they receive it and their next packet has the identifier, then add them to the session.
                        attemptHeartbeat(incomingPlayerData);
                        break;
                    case (byte)1:
                        //If player 1 already been stored in the session, update it.
                        //Otherwise, create add the player to the session.
                        if(GameServer.gameData.playerExists(data[0]))
                            GameServer.gameData.clientUpdate(data);
                        else
                            GameServer.gameData.clientJoin(data, incomingPlayerData.getAddress(), incomingPlayerData.getPort());
                        break;
                    case (byte)2:
                        //If player 2 already been stored in the session, update it.
                        //Otherwise, create add the player to the session.
                        if(GameServer.gameData.playerExists(data[0]))
                            GameServer.gameData.clientUpdate(data);
                        else
                            GameServer.gameData.clientJoin(data,  incomingPlayerData.getAddress(), incomingPlayerData.getPort());
                        break;

                    case (byte) 3:
                        //Drop player 1
                        if(GameServer.gameData.gameClients[0] != null)
                            GameServer.gameData.gameClients[0].endHandle();
                    case (byte) 4:
                    //Drop player 2
                    if(GameServer.gameData.gameClients[1] != null)
                        GameServer.gameData.gameClients[1].endHandle();
                    default:
                        break;
                }  

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("LISTENER ENCOUNTERED AN ERROR");
            }

        }

    }
}
