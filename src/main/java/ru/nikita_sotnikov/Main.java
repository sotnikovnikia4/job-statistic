package ru.nikita_sotnikov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final String SYNC_COMMAND = "sync";
    private static final String SAVE_COMMAND = "save";
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args){
        try{
            runApp(args);
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    private static void runApp(String[] args) throws Exception{
        Configuration configuration = Configuration.create("application.properties");

        SyncService syncService = configuration.getSyncService();
        SaveService saveService = configuration.getSaveService();

        if(args == null || args.length != 2){
            throw new IllegalArgumentException("Wrong number of arguments, expected 2.");
        }

        switch(args[0]){
            case SYNC_COMMAND:{
                syncService.sync(args[1]);
                break;
            }
            case SAVE_COMMAND:{
                saveService.save(args[1]);
                break;
            }
            default:
            {
                System.out.printf("Operation %s is not supported!\n", args[1]);
            }
        }
    }
}