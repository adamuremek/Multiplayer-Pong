package com.gameserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

//TODO: Jordan
public class ClientListener extends Thread {

    DatagramSocket socket;
    DatagramPacket rcvPacket;
    GameData gameData;
    byte[] buf = new byte[600];

    public ClientListener(int port, GameData gameData) throws IOException {
        socket = new DatagramSocket(port);
        this.gameData = gameData;
    }

    private void endHandle(){
        this.interrupt();
    }

    @Override
    public void run() {

        while (!isInterrupted()) {
            try {
                System.out.println("Waiting for incoming connections");
                rcvPacket = new DatagramPacket(buf, buf.length);
                socket.receive(rcvPacket);

                // Create threads so server can handle multiple clients at once.
                if (!gameData.isFull())
                    new ClientHandle(socket, rcvPacket, gameData);

            } catch (IOException e) {
                System.out.println(e.getClass().getCanonicalName());
                System.out.println("HANDLE INTERRUPTED");
            }

        }

    }
}
