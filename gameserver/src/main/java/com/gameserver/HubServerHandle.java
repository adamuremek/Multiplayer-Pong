package com.gameserver;
import java.net.*;
import java.io.*;

public class HubServerHandle extends Thread{
    private Socket sock;
    private DataOutputStream out;
    private DataInputStream in;

    private enum MessageType{
        NONE(0),
        IDENTIFIER_GAME_CLIENT(1),
        IDENTIFIER_GAME_SERVER(2),
        SET_IDENTIFIER(3),
        ADD_GAMESERVER(4),
        MODIFY_GAMESERVER(5),
        DROP_GAMESERVER(6),
        GET_SERVER_LIST(7),
        SERVER_LIST(8);

        private int value;

        MessageType(int b){
            this.value = b;
        }

        public int intValue(){
            return this.value;
        }
    }


    public HubServerHandle(String serverAddr, int serverPort) throws IOException{
        //Create socket and get server state reference
        sock = new Socket(serverAddr, serverPort);
        out = new DataOutputStream(sock.getOutputStream());
        in = new DataInputStream(sock.getInputStream());

        //Send an identifier heartbeat to the hub server
        out.writeByte(MessageType.IDENTIFIER_GAME_SERVER.intValue());

        //Wait for unique server identifier from hub server
        GameServer.serverInfo.identifier = in.readInt();

        //Send current server state
        byte[] serverInfo = GameServer.serverInfo.serialize();
        out.writeByte(MessageType.ADD_GAMESERVER.intValue());
        out.writeInt(serverInfo.length);
        out.write(serverInfo);
    }

    public void endHandle(){
        try {
            in.close();
            out.close();
            sock.close();
        } catch (Exception e) {
            System.out.println("HUB HANDLE END FAILED");
        }
    }

    public void sendModify(){
         try {
            //Send current server state
            byte[] serverInfo = GameServer.serverInfo.serialize();
            out.writeByte(MessageType.MODIFY_GAMESERVER.intValue());
            out.writeInt(serverInfo.length);
            out.write(serverInfo);
         } catch (Exception e) {
            System.out.println("UPDATE FAILED");
            endHandle();
         }
    }

    public void drop(){
        try {
            //End server connection
            out.writeByte(MessageType.DROP_GAMESERVER.intValue());
            endHandle();
         } catch (Exception e) {
            System.out.println("DROP FAILED");
            endHandle();
         }
    }
    
}
