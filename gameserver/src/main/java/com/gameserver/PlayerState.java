package com.gameserver;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PlayerState {
    public static final short PLAYER_STATE_BUF_SIZE = 42;
    //0 is unassigned, 1 is player 1, 2 is player 2
    public byte playerIdentifier;
    public String playerName;
    public byte[] playerColor = {(byte)255, (byte)255, (byte)255};
    public float paddlePosX;
    public float paddlePosY;


    public PlayerState(byte[] playerStateData){
        this.playerIdentifier = playerStateData[0];
        this.playerName = new String(Arrays.copyOfRange(playerStateData, 1, 31), StandardCharsets.US_ASCII);
        this.playerColor = Arrays.copyOfRange(playerStateData, 31, 34);
        this.paddlePosX = ByteBuffer.wrap(Arrays.copyOfRange(playerStateData, 34, 38)).getFloat();
        this.paddlePosY = ByteBuffer.wrap(Arrays.copyOfRange(playerStateData, 38, 42)).getFloat();
    }
}
