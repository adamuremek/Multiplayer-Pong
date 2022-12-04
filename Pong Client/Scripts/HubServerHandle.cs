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
    private Socket _sock;
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
            IPHostEntry host = Dns.GetHostEntry(serverAddr);
            IPAddress ip = host.AddressList[0];
            IPEndPoint ep = new IPEndPoint(ip, port);

            _sock = new Socket(ep.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            _sock.Connect(ep);


            byte[] identifier = { (byte)MessageType.IDENTIFIER_GAME_CLIENT };
            _sock.Send(identifier);

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
        byte[] reqMssg = { (byte)MessageType.GET_SERVER_LIST };
        GD.Print(reqMssg[0]);
        _sock.Send(reqMssg);
    }

    void gameserverAdd()
    {
        //Get data size
        byte[] buff = new byte[4];
        _sock.Receive(buff, buff.Length, SocketFlags.None);
        int dataSize = BitConverter.ToInt32(buff, 0);

        //Get data
        buff = new byte[dataSize];
        _sock.Receive(buff, buff.Length, SocketFlags.None);

        ServerInfo newInfo = new ServerInfo(buff);
        Global.gameServers.Add(newInfo.identifier, newInfo);
    }

    void gameserverModify()
    {
        //Get data size
        byte[] buff = new byte[4];
        _sock.Receive(buff, buff.Length, SocketFlags.None);
        int dataSize = BitConverter.ToInt32(buff, 0);

        //Get data
        buff = new byte[dataSize];
        _sock.Receive(buff, buff.Length, SocketFlags.None);

        //Modify the corresponding server info
        ServerInfo currentInfo = Global.gameServers[ServerInfo.getIdentifier(buff)];
        currentInfo.updateData(buff);
    }

    void gameserverDrop()
    {
        //Get identifier
        byte[] buff = new byte[4];
        _sock.Receive(buff, buff.Length, SocketFlags.None);
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
                byte[] mssgType = new byte[1];
                _sock.Receive(mssgType, mssgType.Length, SocketFlags.None);
                GD.Print(mssgType[0]);
                GD.Print("DESIRED: " + ((byte)MessageType.ADD_GAMESERVER).ToString());
                switch (mssgType[0])
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
