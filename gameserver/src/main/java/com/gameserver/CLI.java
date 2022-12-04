package com.gameserver;

import java.io.IOException;

public class CLI extends Thread{
    private static final int REFRESH_RATE = 5000;
    private boolean isActive;

    public CLI(){
        this.isActive = true;
        this.start();
    }

    private String generateHeader(){
        return String.join("\n", 
        "================================================================",
        " _____   ____  _   _  _____",
        "|  __ \\ / __ \\| \\ | |/ ____|",
        "| |__) | |  | |  \\| | |  __  ",
        "|  ___/| |  | | . ` | | |_ |",
        "| |    | |__| | |\\  | |__| |",
        "|_|____ \\____/|_| \\_|\\_____|    _____",
        " / ____|                       / ____|",
        "| |  __  __ _ _ __ ___   ___  | (___   ___ _ ____   _____ _ __",
        "| | |_ |/ _` | '_ ` _ \\ / _ \\  \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|",
        "| |__| | (_| | | | | | |  __/  ____) |  __/ |   \\ V /  __/ |",
        " \\_____|\\__,_|_| |_| |_|\\___| |_____/ \\___|_|    \\_/ \\___|_|",
        "================================================================");        
           
                                                                       
                                                                       
    }

    private String getStats(){
        String out = "";
        out += String.format("Server Hosted on Port: %d\n\n", GameServer.serverInfo.serverPort) + "\n";
        out += GameServer.gameData.gameClients[0] != null ? String.format("Player 1: %s || SCORE: %d\n", GameServer.gameData.gameClients[0].playerName, GameServer.gameData.player1Score) : String.format("Player 1: %s || SCORE: %d\n", "", GameServer.gameData.player1Score);
        out += GameServer.gameData.gameClients[1] != null ? String.format("Player 2: %s || SCORE: %d\n", GameServer.gameData.gameClients[1].playerName, GameServer.gameData.player2Score) : String.format("Player 2: %s || SCORE: %d\n", "", GameServer.gameData.player2Score);
        return out;
    }

    private void clearConsole() throws InterruptedException, IOException{
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
        pb.inheritIO().start().waitFor();
    }

    public void endCLI(){
        try {
            interrupt();
            isActive = false;
        } catch (Exception e) {
            System.out.println("ERROR IN END CLI " + e.getClass().getCanonicalName());
        }
    }

    @Override
    public void run(){
        try {
            while(this.isActive){
                clearConsole();
                System.out.println(String.format("%s\n\n\n", generateHeader()));
                System.out.println(getStats());

                Thread.sleep(REFRESH_RATE);
            }
        } catch (Exception e) {
            System.out.println("CLI ERROR");
        }
        System.out.println("CLI THREAD ENDED");
    }
}
