package ru.nikita_sotnikov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class SaveService {
    private static final Logger log = LoggerFactory.getLogger(SaveService.class);
    private final DBOperations dbOperations;
    private final XmlParser xmlParser;

    public SaveService(DBOperations dbOperations, XmlParser xmlParser) {
        this.dbOperations = dbOperations;
        this.xmlParser = xmlParser;
    }

    void save(String fileName) throws Exception {
        log.info("Saving into file '{}'", fileName);

        List<Job> jobs = dbOperations.getJobList();

        Document document = xmlParser.saveToDocument(jobs);

        DOMSource domSource = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        log.info("Opening file '{}'", fileName);
        try (OutputStream out = new FileOutputStream(fileName)) {
            transformer.transform(domSource, new StreamResult(out));
        }

        String resultInfo = String.format("Saved to file '%s'. %d jobs saved", fileName, jobs.size());
        log.info(resultInfo);
        System.out.println(resultInfo);
    }
}
