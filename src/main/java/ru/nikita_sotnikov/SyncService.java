package ru.nikita_sotnikov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service class responsible for synchronizing job data between an XML file and the database.
 */
public class SyncService {
    private static final Logger log = LoggerFactory.getLogger(SyncService.class);
    private final XmlParser xmlParser;
    private final DBOperations dbOperations;

    /**
     * Constructs a new SyncService with the specified XML parser and database operations.
     *
     * @param xmlParser An instance of {@link XmlParser} to parse XML files.
     * @param dbOperations An instance of {@link DBOperations} to interact with the database.
     */
    public SyncService(XmlParser xmlParser, DBOperations dbOperations) {
        this.dbOperations = dbOperations;
        this.xmlParser = xmlParser;
    }

    /**
     * Synchronizes the database table content with the provided XML file.
     * The process involves:
     * <ol>
     * <li>Parsing job data from the specified XML file.</li>
     * <li>Retrieving current job data from the database.</li>
     * <li>Comparing the two sets of data to identify jobs to be inserted, updated, or deleted.</li>
     * <li>Performing all identified database modifications (insertions, updates, deletions) within a single transaction.</li>
     * </ol>
     * The synchronization occurs based on the natural key (DepCode, DepJob).
     * Error handling includes checking for duplicate natural keys in the XML file.
     * Logging messages are used to track the progress of the synchronization operation.
     * A brief summary of the operation result is printed to the console.
     *
     * @param fileName The name of the XML file from which data will be synchronized.
     * @throws Exception If an error occurs during XML parsing, database operations,
     * or if duplicate natural keys are found in the XML file.
     */
    public void sync(String fileName) throws Exception {
        log.info("Start synchronization from file '{}'", fileName);

        Map<JobKey, Job> jobsFromFile = xmlParser.parse(fileName);
        Map<JobKey, Job> jobsFromDB = dbOperations.getJobMap();

        List<Job> insertList = new ArrayList<>();
        List<Job> updateList = new ArrayList<>();
        for(var entry : jobsFromFile.entrySet()) {
            Job toAdd = entry.getValue();
            Job jobFromDB = jobsFromDB.get(entry.getKey());

            if(jobFromDB != null){
                if(!Objects.equals(toAdd.getDescription(), jobFromDB.getDescription())){
                    toAdd.setId(jobsFromDB.get(entry.getKey()).getId());
                    updateList.add(toAdd);
                }
                jobsFromDB.remove(entry.getKey()); // Remove from DB map if found in XML, remaining are to be deleted
            }
            else{
                insertList.add(toAdd); // Job is new, add to insert list
            }
        }

        List<Job> deleteList = new ArrayList<>(jobsFromDB.values()); // Remaining jobs in jobsFromDB are those not in XML
        log.info("Created lists with insertions, updates and deletions");

        dbOperations.refreshDB(insertList, updateList, deleteList);

        String resultInfo = String.format("Inserted: %d, updated: %d, deleted: %d, total: %d.", insertList.size(), updateList.size(), deleteList.size(), jobsFromFile.size());
        log.info(resultInfo);
        System.out.println(resultInfo);
    }
}