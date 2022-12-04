using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.Net.Sockets;
using System.Net;
using Godot;

public class GameServerHandle
{
    private const short TIMEOUT_DURATION = 10;
    private short currentTimeoutCount = 0;
    public bool isActive { get; private set; } = false;
    private UdpClient connection;
    private IPEndPoint ep;

    public GameServerHandle()
    {
    }

    public void connect(string host, int port)
    {
        try
        {
            //Establish Connection
            connection = new UdpClient();
            connection.Connect(host, port);

            //Send request-identifier packet
            byte[] currentPlayerState = Global.playerState.serialize();
            connection.Send(currentPlayerState, currentPlayerState.Length);
            ep = new IPEndPoint(IPAddress.Any, 0);

            currentTimeoutCount = TIMEOUT_DURATION;
            System.Threading.Thread t1 = new System.Threading.Thread(new ThreadStart(timeoutTicker));
            isActive = true;
            t1.Start();

            //Pull identifier
            byte[] dataWithIdentifier = connection.Receive(ref ep);
            Global.playerState.playerIdentifier = dataWithIdentifier[0];

            //Send confimration packet with accepted identifier
            currentPlayerState = Global.playerState.serialize();
            connection.Send(currentPlayerState, currentPlayerState.Length);

            //Start listening to all incoming data
            System.Threading.Thread t2 = new System.Threading.Thread(new ThreadStart(recieveData));
            Global.loadGameScene();


        }
        catch (Exception e)
        {
            GD.Print("GAME SERVER CONNECTION FAILED");
        }
    }

    public void drop()
    {
        if(Global.playerState.playerIdentifier != 0)
        {
            byte[] dropMssg = { (byte)(Global.playerState.playerIdentifier + 2) };
            connection.Send(dropMssg, dropMssg.Length);
        }
    }

    public void updateServer()
    {
        byte[] currentPlayerState = Global.playerState.serialize();
        connection.Send(currentPlayerState, currentPlayerState.Length);
    }


    public void endHandle()
    {
        if (!isActive)
            return;

        isActive = false;
        drop();
        Global.playerState.paddlePosY = 240f;
        Input.MouseMode = Input.MouseModeEnum.Visible;
        connection.Close();
    }

    void recieveData()
    {
        while (isActive)
        {
            try
            {
                byte[] incomingData = connection.Receive(ref ep);
                Global.gameState.updateData(incomingData);
            }
            catch(Exception e)
            {
                GD.Print("GAME SERVER CONNECTION TERMINATED");
                endHandle();
                return;
            }
        }
    }

    void timeoutTicker()
    {
        while (isActive)
        {
            try
            {
                if(currentTimeoutCount == 0)
                {
                    endHandle();
                    return;
                }

                System.Threading.Thread.Sleep(1000);
                currentTimeoutCount--;
                
            } 
            catch(Exception e)
            {
                GD.Print("SERVER TIMED OUT");
                endHandle();
                return;
            }
        }
    }
}
