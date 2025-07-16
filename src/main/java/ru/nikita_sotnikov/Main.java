package ru.nikita_sotnikov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLOutput;

/**
 * The main entry point for the application.
 * This class parses command-line arguments to determine whether to perform a
 * synchronization or save operation, and then invokes the appropriate service.
 * It also handles global exception logging and displays brief error messages to the console.
 * <p>
 * Expected command-line arguments:
 * <ul>
 * <li>{@code sync <fileName>} to synchronize the database with the XML file.</li>
 * <li>{@code save <fileName>} to save the database content to an XML file.</li>
 * </ul>
 * </p>
 */
public class Main {
    private static final String SYNC_COMMAND = "sync";
    private static final String SAVE_COMMAND = "save";
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * The main method that serves as the application's entry point.
     * It calls {@link #runApp(String[])} and catches any exceptions that occur during execution,
     * logging them and printing a concise error message to the console.
     *
     * @param args Command-line arguments specifying the operation and file name.
     */
    public static void main(String[] args){
        try{
            runApp(args);
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    /**
     * Executes the main application logic based on the provided command-line arguments.
     * It initializes the application configuration, retrieves the appropriate services,
     * and performs either a synchronization or save operation.
     *
     * @param args Command-line arguments. Expected format: {@code <command> <fileName>}.
     * Supported commands are "sync" and "save".
     * @throws IllegalArgumentException If an incorrect number of arguments is provided.
     * @throws Exception If any error occurs during configuration loading, service execution,
     * or file operations.
     */
    private static void runApp(String[] args) throws Exception{
        // Creates a Configuration object by loading properties from "application.properties".
        // This file contains database connection parameters and logging settings.
        Configuration configuration = Configuration.create("application.properties");

        // Retrieves the SyncService and SaveService instances from the configuration.
        // These services are responsible for performing synchronization and saving operations, respectively.
        SyncService syncService = configuration.getSyncService();
        SaveService saveService = configuration.getSaveService();

        // Validates the number of command-line arguments.
        // The application expects exactly two arguments: the command and the file name.
        if(args == null || args.length != 2){
            throw new IllegalArgumentException("Wrong number of arguments, expected 2.");
        }

        // Uses a switch statement to determine which operation to perform based on the first argument.
        switch(args[0]){
            case SYNC_COMMAND:{
                // If the command is "sync", it invokes the sync method of the SyncService,
                // passing the file name from the second argument.
                // The synchronization process involves updating the database based on the XML file.
                syncService.sync(args[1]);
                break;
            }
            case SAVE_COMMAND:{
                // If the command is "save", it invokes the save method of the SaveService,
                // passing the file name from the second argument.
                // This function exports the contents of the database table to an XML file.
                saveService.save(args[1]);
                break;
            }
            default:
            {
                // If an unsupported command is provided, an error message is printed to the console.
                // Brief information about the operation result should be displayed on the screen.
                System.err.printf("Operation %s is not supported!\n", args[0]);
                logger.error("Operation {} is not supported!", args[0]);
            }
        }
    }
}