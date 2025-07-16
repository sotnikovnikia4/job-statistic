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

/**
 * Service class responsible for saving a list of job objects to an XML file.
 */
public class SaveService {
    private static final Logger log = LoggerFactory.getLogger(SaveService.class);
    private final DBOperations dbOperations;
    private final XmlParser xmlParser;

    /**
     * Constructs a new SaveService with the specified database operations and XML parser.
     *
     * @param dbOperations An instance of {@link DBOperations} to retrieve job data from the database.
     * @param xmlParser An instance of {@link XmlParser} to handle XML document creation from job objects.
     */
    public SaveService(DBOperations dbOperations, XmlParser xmlParser) {
        this.dbOperations = dbOperations;
        this.xmlParser = xmlParser;
    }

    /**
     * Saves a list of job objects, retrieved from the database, into an XML file.
     * A brief summary of the operation is also printed to the console.
     *
     * @param fileName The name of the file where the XML data will be saved.
     * @throws Exception If an error occurs during database operations, XML parsing,
     * or file writing.
     */
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