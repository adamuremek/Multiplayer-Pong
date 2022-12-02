package com.gameserver;

import java.util.LinkedList;

public class GameData {
    public LinkedList<ClientHandle> gameClients = new LinkedList<>();
    public ServerInfo serverInfo;
    public Vector2 ballVelDir = new Vector2();
    public Vector2 p1VecPos = new Vector2();
    public Vector2 p2VecPos = new Vector2();
    public Vector2 ballVecPos = new Vector2();
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

    public Vector2 getP1VecPos() {
        return p1VecPos;
    }

    public void update() {
        // TODO update values from data
    }
    
}
