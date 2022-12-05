package com.hubserver;

import java.util.Arrays;
import java.util.HashMap;

public class HubServer{
    static HubData hub;
    static CLI cli;
    static ClientListener cl;
    static HashMap<String, String> argTable;


    static class Cleanup extends Thread{
        private HubData hub;
        private CLI cli;
        private ClientListener cl;
        
        public Cleanup(HubData hub, CLI cli, ClientListener cl){
            this.hub = hub;
            this.cli = cli;
            this.cl = cl;
        }

        @Override
        public void run(){
            this.cl.endListener();
            this.hub.disconnectAllClients();
            this.cli.endCLI();
            System.out.println("CLEANUP DONE");
        }
    }

    public static boolean parseArgs(String[] args){
        //Initialize the arg table and a set of valid keys for checking
        argTable = new HashMap<String, String>();
        String[] validKeys = {"port"};

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

        hub = new HubData(Integer.parseInt(argTable.get("port")));
        cl = new ClientListener(hub.getServerPort(), hub);
        cli = new CLI(hub);

        //Shutdown hook to clean up ports and other nonsense
        Runtime.getRuntime().addShutdownHook(new Cleanup(hub, cli, cl));


        cli.join();
    }
}