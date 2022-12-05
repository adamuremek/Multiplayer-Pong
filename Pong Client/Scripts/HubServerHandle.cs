using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Godot;
using System.Threading;
using System.Net;
using System.Net.Sockets;

public class HubServerHandle
{
    private TcpClient _sock;
    private NetworkStream stream;
    public bool isActive { get; private set; } = false;

    public enum MessageType : byte
    {
        NONE = 0,
        IDENTIFIER_GAME_CLIENT = 1,
        IDENTIFIER_GAME_SERVER = 2,
        SET_IDENTIFIER = 3,
        ADD_GAMESERVER = 4,
        MODIFY_GAMESERVER = 5,
        DROP_GAMESERVER = 6,
        GET_SERVER_LIST = 7,
        SERVER_LIST = 8


    }

    public HubServerHandle()
    {
    }

    public bool connect(string serverAddr, int port)
    {
        try
        {
            _sock = new TcpClient();
            _sock.Connect(serverAddr, port);
            stream = _sock.GetStream();

            stream.WriteByte((byte)MessageType.IDENTIFIER_GAME_CLIENT);
            isActive = true;

            System.Threading.Thread t = new System.Threading.Thread(new ThreadStart(listenerCallback));
            t.Start();
            return true;
        }
        catch (Exception e)
        {
            GD.Print("HUB CONNECTION FAILED");
            return false;
        }
    }

    public void endHandle()
    {
        if (!isActive || _sock == null)
            return;

        isActive = false;
        _sock.Close();
    }

    public void requestServerList()
    {
        stream.WriteByte((byte)MessageType.GET_SERVER_LIST);
    }

    void gameserverAdd()
    {
        //Get data size

        byte[] buff = new byte[4];
        stream.Read(buff, 0, buff.Length);
        Array.Reverse(buff);
        int dataSize = BitConverter.ToInt32(buff, 0);

        //Get data
        buff = new byte[dataSize];
        stream.Read(buff, 0, buff.Length);

        ServerInfo newInfo = new ServerInfo(buff);
        GD.Print("IDENTIFIER: " + newInfo.identifier);
        Global.gameServers.Add(newInfo.identifier, newInfo);
    }

    void gameserverModify()
    {
        //Get data size
        byte[] buff = new byte[4];
        stream.Read(buff, 0, buff.Length);
        Array.Reverse(buff);
        int dataSize = BitConverter.ToInt32(buff, 0);

        //Get data
        buff = new byte[dataSize];
        stream.Read(buff, 0, buff.Length);

        //Modify the corresponding server info
        ServerInfo currentInfo = Global.gameServers[ServerInfo.getIdentifier(buff)];
        currentInfo.updateData(buff);
    }

    void gameserverDrop()
    {
        //Get identifier
        byte[] buff = new byte[4];
        stream.Read(buff, 0, buff.Length);
        Array.Reverse(buff);
        int identifier = BitConverter.ToInt32(buff, 0);

        //Remove server from list
        ServerInfo droppedServer = Global.gameServers[identifier];
        Global.gameServers.Remove(identifier);
        droppedServer.remove();
    }

    void listenerCallback()
    {
        while (isActive)
        {
            try
            {
                byte mssgType = (byte)stream.ReadByte();
                GD.Print("MESSAGE TYPE: " + mssgType);
                switch (mssgType)
                {
                    case (byte)MessageType.ADD_GAMESERVER:
                        GD.Print("ADDING");
                        gameserverAdd();
                        break;
                    case (byte)MessageType.MODIFY_GAMESERVER:
                        gameserverModify();
                        break;
                    case (byte)MessageType.DROP_GAMESERVER:
                        gameserverDrop();
                        break;
                }
            }
            catch (Exception e)
            {
                GD.Print(e.StackTrace);
                GD.Print("Socket broke");
            }
        }
    }

}
