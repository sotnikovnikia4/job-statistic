package ru.nikita_sotnikov;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyncService {
    private final XmlParser xmlParser;
    private final DBOperations dbOperations;

    public SyncService(XmlParser xmlParser, DBOperations dbOperations) {
        this.dbOperations = dbOperations;
        this.xmlParser = xmlParser;
    }

    public void sync(String fileName) throws Exception {
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

        dbOperations.refreshDB(insertList, updateList, deleteList);
    }
}
