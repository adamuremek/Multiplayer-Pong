package com.hubserver;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ClientListener extends Thread{
    private ServerSocket serverSock;
    private HubData hub;
    private boolean isActive;

    public ClientListener(int serverPort, HubData hub) throws IOException{
        this.serverSock = new ServerSocket(serverPort);
        this.hub = hub;
        this.isActive = true;
        this.start();
    }

    public void endListener(){
        try {
            System.out.println("STINKY FAGIT");
            this.isActive = false;
            serverSock.close();
        } catch (Exception e) {
            System.out.println("ERROR IN END LISTENER CALL " + e.getClass().getCanonicalName());
        }
    }

    @Override
    public void run(){
        while(this.isActive){
            try {
                Socket s = serverSock.accept();
                System.out.println("INCOMING CLIENT");
                new ClientHandle(s, hub);
            } catch (SocketException e) {
                System.out.println("Good error");
            } catch (EOFException e){
                System.out.println("Client disconenected or something");
            }
            catch(Exception e){
                System.out.println("SOME OTHER ERROR");
            }
        }
        

        System.out.println("LISTENER THREAD ENDED");
    }
}
