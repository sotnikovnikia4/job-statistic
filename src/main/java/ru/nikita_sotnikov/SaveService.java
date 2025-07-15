package ru.nikita_sotnikov;

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
    private final DBOperations dbOperations;
    private final XmlParser xmlParser;

    public SaveService(DBOperations dbOperations, XmlParser xmlParser) {
        this.dbOperations = dbOperations;
        this.xmlParser = xmlParser;
    }

    void save(String fileName) throws Exception {
        List<Job> jobs = dbOperations.getJobList();

        Document document = xmlParser.saveToDocument(jobs);

        DOMSource domSource = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        try (OutputStream out = new FileOutputStream(fileName)) {
            transformer.transform(domSource, new StreamResult(out));
        }
    }
}
