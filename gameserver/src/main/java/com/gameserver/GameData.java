package com.gameserver;

import java.util.LinkedList;

import com.gameserver.Paddle.Side;

public class GameData {
    public LinkedList<ClientHandle> gameClients = new LinkedList<>();
    public ServerInfo serverInfo;
    public Ball ball = new Ball();
    public Paddle p1 = new Paddle(Side.LEFT);
    public Paddle p2 = new Paddle(Side.RIGHT);
    public int p1score = 0;
    public int p2score = 0;

    public GameData(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public void addClient(ClientHandle client, String name) {
        if (!isFull()) {
            this.gameClients.add(client);
            if (serverInfo.player1Name.equals(""))
                serverInfo.player1Name = name;
            else
                serverInfo.player2Name = name;
        }

    }

    public boolean isFull() {
        return gameClients.size() == 2;
    }

    public void update() {
        // TODO update values from data
    }
    
}
