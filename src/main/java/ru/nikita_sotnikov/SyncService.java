package ru.nikita_sotnikov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyncService {
    private static final Logger log = LoggerFactory.getLogger(SyncService.class);
    private final XmlParser xmlParser;
    private final DBOperations dbOperations;

    public SyncService(XmlParser xmlParser, DBOperations dbOperations) {
        this.dbOperations = dbOperations;
        this.xmlParser = xmlParser;
    }

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
                if((toAdd.getDescription() == null && jobFromDB.getDescription() == null) || !toAdd.getDescription().equals(jobFromDB.getDescription())){
                    toAdd.setId(jobsFromDB.get(entry.getKey()).getId());
                    updateList.add(toAdd);
                }
                jobsFromDB.remove(entry.getKey());
            }
            else{
                insertList.add(toAdd);
            }
        }

        List<Job> deleteList = new ArrayList<>(jobsFromDB.values());
        log.info("Created lists with insertions, updates and deletions");

        dbOperations.refreshDB(insertList, updateList, deleteList);

        String resultInfo = String.format("Inserted: %d, updated: %d, deleted: %d, total: %d.", insertList.size(), updateList.size(), deleteList.size(), jobsFromFile.size());
        log.info(resultInfo);
        System.out.println(resultInfo);
    }
}
