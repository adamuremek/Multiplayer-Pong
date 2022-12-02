package com.hubserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ClientHandle extends Thread{
    private Socket sock;
    private HubData hub;
    private DataOutputStream out;
    private DataInputStream in;
    private int serverIdentifier;
    private boolean isActive;

    public enum MessageType{
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

    public ClientHandle(Socket sock, HubData hub) throws IOException{
        //Setup socket
        this.sock = sock;
        this.hub = hub;
        this.serverIdentifier = 0;
        this.isActive = true;
        out = new DataOutputStream(sock.getOutputStream());
        in = new DataInputStream(sock.getInputStream());
        

        //Get identifyer byte of the incoming connnection
        System.out.println("AVAILABLE: " + in.available());
        byte id = in.readByte();
        //Start the thread
        this.start();

        //Evaluate handle type and initialize handle
        
        System.out.println(id);
        MessageType mssgType = byteToMssg(id);
        switch(mssgType){
            //If the first byte is 1, its a game client
            case IDENTIFIER_GAME_CLIENT:
                System.out.println("NEW CLENT");
                hub.addGameClient(this);
                hub.sendServerListToClient(this);
                break;
            //If the first byte is 2, its a game server
            case IDENTIFIER_GAME_SERVER:
                System.out.println("NEW SERVER");
                hub.addGameServer(this);
                this.serverIdentifier = hub.generateIdentifier();
                //Generate identifier and send it to client
                if(this.serverIdentifier != -1)
                    send(ByteBuffer.allocate(4).putInt(this.serverIdentifier).array(), MessageType.SET_IDENTIFIER);
                else
                    endHandle();
                break;
                
            default:
                System.out.println(mssgType);
                break;
        }
    }

    private MessageType byteToMssg(byte b){
        switch(b){
            case (byte)1:
                return MessageType.IDENTIFIER_GAME_CLIENT;
            case (byte)2:
                return MessageType.IDENTIFIER_GAME_SERVER;
            case (byte)3:
                return MessageType.SET_IDENTIFIER;
            case (byte)4:
                return MessageType.ADD_GAMESERVER;
            case (byte)5:
                return MessageType.MODIFY_GAMESERVER;
            case (byte)6:
                return MessageType.DROP_GAMESERVER;
            default:
                return MessageType.NONE;
        }
    }
    
    public void endHandle(){
        if(!this.isActive)
            return;

        try {
            this.isActive = false;
            out.close();
            in.close();
            sock.close();
        } catch (Exception e) {
            System.out.println("ERROR IN CLIENT HANDLE END");
        } finally{
            if(serverIdentifier == 0)
                hub.dropGameClient(this);
            else
                hub.dropGameServer(this, this.serverIdentifier);
        }
    }

    public void send(byte[] data, MessageType mssgType){
        try {
            //Send Data in the following order: byte flag, data length, data bytes
            out.writeByte(mssgType.intValue());
            out.writeInt(data.length);
            out.write(data);
        } catch (Exception e) {
            System.out.println("SEND FAILED");
        }
    }

    @Override
    public void run(){
        while(this.isActive){
            try {
                 //Server identifier of 0 implies a game client, otherwise its a game server
                 if(serverIdentifier == 0){
                    MessageType mssgType = byteToMssg(in.readByte());

                    switch(mssgType){
                        //Game client requests server list
                        case GET_SERVER_LIST:
                            hub.sendServerListToClient(this);
                            break;
                        
                        default:
                            break;
                    }
                }
                else{
                    MessageType mssgType = byteToMssg(in.readByte());
                    byte[] data;
                    switch(mssgType){
                        //Game server info is new
                        case ADD_GAMESERVER:
                            data = new byte[in.readInt()];
                            in.readFully(data);
                            hub.notifyGameClientsAdd(this.serverIdentifier, data);
                            break;
                        //Gane server info is modified
                        case MODIFY_GAMESERVER:
                            data = new byte[in.readInt()];
                            in.readFully(data);

                            break;
                        //Game server drops
                        case DROP_GAMESERVER:
                            endHandle();
                            break;

                        default:
                            break;
                    }
                }
            } catch (EOFException e){
                endHandle();
                System.out.println("SOME SHIT HEPAEIJOIFEJN");
            } catch (SocketException e){
                endHandle();
                System.out.println("socket err");
            } catch (Exception e) {
                System.out.println("HANDLE INTERRUPTED " + e.getClass().getCanonicalName());
            }
        }

        System.out.println("HANDLE THREAD ENDED");
    }
}