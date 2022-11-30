package com.hubserver;

import java.io.IOException;

//ASCII ART SHIT https://textkool.com/en/ascii-art-generator?hl=default&vl=default&font=Big&text=PONG%0AGame%20Server

public class CLI extends Thread{
    private static final int REFRESH_RATE = 5000;
    private HubData hub;
    private boolean isActive;

    public CLI(HubData hub){
        this.hub = hub;
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
        "|_|   _ \\____/|_| \\_|\\_____|",
        "| |  | |     | |      / ____|",
        "| |__| |_   _| |__   | (___   ___ _ ____   _____ _ __",
        "|  __  | | | | '_ \\   \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|",
        "| |  | | |_| | |_) |  ____) |  __/ |   \\ V /  __/ |",
        "|_|  |_|\\__,_|_.__/  |_____/ \\___|_|    \\_/ \\___|_|",
        "================================================================");                                                   
    }

    private String getStats(){
        return String.join("\n", 
        String.format("Server Hosted on Port: %d\n\n", hub.getServerPort()),
        String.format("Connected Game Servers: %d", hub.getGameServerCount()),
        String.format("Connected Game Clients: %d", hub.getGameClientCount()));
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
            System.out.println("SHIT HAPPENED HERE " + e.getClass().getCanonicalName());
        }
        System.out.println("CLI THREAD ENDED");
    }
}
