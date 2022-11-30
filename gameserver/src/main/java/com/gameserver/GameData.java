package com.gameserver;

import java.util.LinkedList;

public class GameData {
    private LinkedList<ClientHandle> gameClients = new LinkedList<>();
    private ServerInfo serverInfo;

    private Vector2 ballVelDir = new Vector2();

    private Vector2 p1VecPos = new Vector2();
    private Vector2 p2VecPos = new Vector2();
    private Vector2 ballVecPos = new Vector2();

    private int[] score = new int[2];

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

    public Vector2 getP2VecPos() {
        return p2VecPos;
    }

    public Vector2 getBallVecPos() {
        return ballVecPos;
    }

    public Vector2 getBallVelDir() {
        return ballVelDir;
    }

    public int[] getScore() {
        return score;
    }

    public void setP1VecPos(Vector2 p1vecPos) {
        this.p1VecPos = p1vecPos;
    }

    public void setP2VecPos(Vector2 p2vecPos) {
        this.p2VecPos = p2vecPos;
    }

    public void setBallVecPos(Vector2 ballVecPos) {
        this.ballVecPos = ballVecPos;
    }

    public void setBallVelDir(Vector2 ballVelDir) {
        this.ballVelDir = ballVelDir;
    }

    public void setScore(int[] score) {
        this.score = score;
    }
    
}
