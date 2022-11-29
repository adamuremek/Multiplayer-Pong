package com.gameserver;
import java.net.*;
import java.io.*;

public class HubServerConnection{
    private Socket sock;
    private ServerInfo serverInfo;
    private DataOutputStream out;
    private DataInputStream in;

    public HubServerConnection(String serverAddr, int serverPort, ServerInfo serverInfo) throws IOException{
        //Create socket and get server state reference
        sock = new Socket(serverAddr, serverPort);
        out = new DataOutputStream(sock.getOutputStream());
        in = new DataInputStream(sock.getInputStream());
        this.serverInfo = serverInfo;

        //Wait for unique server identifier from hub server
        serverInfo.identifier = in.readInt();

        //Send current server state
        out.write(serverInfo.serialize());
    }

    public void sendServerInfo(ServerInfo serverInfo) throws IOException{
        out.write(serverInfo.serialize());
    }
    
}
