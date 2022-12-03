package com.gameserver;

import java.util.Arrays;
import java.util.HashMap;

public class GameServer {
    public static ServerInfo serverInfo = new ServerInfo();
    public static GameData gameData = new GameData();
    public static ClientListener cl;
    public static HubServerHandle hub;
    public static CLI cli;
    static HashMap<String, String> argTable;


    static class Cleanup extends Thread{
        @Override
        public void run(){
            hub.endHandle();
            cl.endListener();
            cli.endCLI();
            System.out.println("CLEANUP DONE");
        }
    }

    public static boolean parseArgs(String[] args){
        //Initialize the arg table and a set of valid keys for checking
        argTable = new HashMap<String, String>();
        String[] validKeys = {"port","addr", "serverName", "hubPort", "hubAddr"};

        //Iterate through arg values
        for(String s : args){
            //If the argument does not contain a splititer, the input was wrong
            if(!s.contains(":"))
                return false;

            String[] pair = s.split(":");

            //If the argument contains an invalid key, break and inform
            if(!Arrays.asList(validKeys).contains(pair[0]))
                return false;

            argTable.put(pair[0], pair[1]);
        }

        //If a proper amount of arguments were not supplied, break and inform
        if(argTable.size() != validKeys.length)
            return false;

        return true;
    }

    public static void main(String[] args) throws Exception{
        if(!parseArgs(args)){
            System.out.println("An invalid set of arguments were supplied to the program.");
            return;
        }

        serverInfo.serverName = argTable.get("serverName");
        serverInfo.serverAddr = argTable.get("addr");
        serverInfo.serverPort = Integer.parseInt(argTable.get("port"));

        cl = new ClientListener(serverInfo.serverPort);
        hub = new HubServerHandle(argTable.get("hubAddr"), Integer.parseInt(argTable.get("hubPort")));
        cli = new CLI();


        //Shutdown hook to clean up ports and other nonsense
        Runtime.getRuntime().addShutdownHook(new Cleanup());

        cli.join();
    }
}
