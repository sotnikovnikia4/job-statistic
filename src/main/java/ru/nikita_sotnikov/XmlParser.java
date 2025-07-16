package ru.nikita_sotnikov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing and creating XML documents related to {@link Job} objects.
 * It supports reading job data from an XML file into a map of {@link JobKey} to {@link Job}
 * and saving a list of {@link Job} objects into an XML {@link Document} structure.
 * XML parsing is done using the DOM technology.
 */
public class XmlParser {
    private static final Logger log = LoggerFactory.getLogger(XmlParser.class);

    /**
     * Parses the specified XML file and extracts job data into a map.
     * The method expects the XML to have a root element named "jobs" containing "job" elements.
     * Each "job" element should contain "depCode", "depJob", and optionally "description" elements.
     * It performs validation on the XML structure and data, including checking for duplicate natural keys.
     *
     * @param fileName The path to the XML file to be parsed.
     * @return A {@link Map} where keys are {@link JobKey} (depCode, depJob) and values are {@link Job} objects.
     * @throws Exception If an error occurs during XML parsing, if the file format is invalid,
     * or if duplicate natural keys are found.
     */
    public Map<JobKey, Job> parse(String fileName) throws Exception {
        log.info("Start parsing.");
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new File(fileName));
        document.getDocumentElement().normalize();

        log.info("Opened file '{}'", fileName);

        Element root = document.getDocumentElement();

        if(!root.getNodeName().equals("jobs")) {
            throw new SAXException("Root element is not 'jobs'");
        }

        NodeList nl = root.getChildNodes();

        Map<JobKey, Job> jobs = new HashMap<>();

        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("job")) {
                NodeList children = node.getChildNodes();

                Job job = createJob(children);
                checkJob(job); // Validate job fields based on database column constraints

                JobKey jobKey = new JobKey(job.getDepCode(), job.getDepJob());

                if(jobs.containsKey(jobKey)) {
                    // Application must issue an error if there are two records with the same natural key in the XML file.
                    throw new SAXException(String.format("Duplicate jobKey: depCode='%s', depJob='%s'.", job.getDepCode(), job.getDepJob()));
                }
                else{
                    jobs.put(jobKey, job);
                }
            }
            else if(node.getNodeType() == Node.ELEMENT_NODE){
                throw new SAXException("Invalid format: element is not 'job' in 'jobs'.");
            }
        }

        log.info("Parsed {} jobs from file '{}'", jobs.size(), fileName);

        return jobs;
    }

    /**
     * Creates a {@link Job} object from a {@link NodeList} of child elements.
     * This method expects elements like "depCode", "depJob", and "description".
     *
     * @param children A {@link NodeList} containing the child elements of a "job" node.
     * @return A new {@link Job} object populated with data from the child nodes.
     * @throws SAXException If duplicate or unsupported fields are detected.
     */
    private Job createJob(NodeList children) throws SAXException {
        Job job = new Job();

        for (int j = 0; j < children.getLength(); j++) {
            Node current = children.item(j);
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                if(current.getNodeName().equals("depCode") && job.getDepCode() == null) {
                    job.setDepCode(current.getTextContent());
                }
                else if(current.getNodeName().equals("depJob") && job.getDepJob() == null) {
                    job.setDepJob(current.getTextContent());
                }
                else if(current.getNodeName().equals("description") && job.getDescription() == null) {
                    job.setDescription(current.getTextContent());
                }
                else{
                    throw new SAXException("Duplicates or not supported fields are detected.");
                }
            }
        }

        return job;
    }

    /**
     * Validates the fields of a {@link Job} object against predefined constraints.
     * Checks for null natural key fields and length constraints for `depCode`, `depJob`, and `description`.
     *
     * @param job The {@link Job} object to be validated.
     * @throws Exception If any validation rule is violated (e.g., null natural key, excessive length).
     */
    private void checkJob(Job job) throws Exception{
        if(job.getDepCode() == null || job.getDepJob() == null) {
            throw new SAXException("Invalid format: depCode='null' or depJob='null'.");
        }
        else if(job.getDepCode().length() > 20){ // DepCode String(длина 20 символов)
            throw new SAXException(String.format("Invalid format: depCode.length > 20, depCode='%s'.", job.getDepCode()));
        }
        else if(job.getDepJob().length() > 100){ // DepJob String(длина 100 символов)
            throw new SAXException(String.format("Invalid format: depJob.length > 100, depJob='%s'.", job.getDepJob()));
        }
        else if(job.getDescription() != null && job.getDescription().length() > 255){ // Description String (длина 255 символов)
            throw new SAXException(String.format("Invalid format: description.length > 255, description='%s'.", job.getDescription()));
        }
    }

    /**
     * Creates an XML {@link Document} from a list of {@link Job} objects.
     * The document will have a root "jobs" element, with each job represented by a "job" element.
     * Each "job" element will contain "depCode", "depJob", and optionally "description" elements.
     *
     * @param jobs A {@link List} of {@link Job} objects to be converted into an XML document.
     * @return A {@link Document} object representing the XML structure of the jobs.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     */
    public Document saveToDocument(List<Job> jobs) throws ParserConfigurationException {
        log.info("Creating xml document.");

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("jobs");
        document.appendChild(root);

        for(Job job : jobs) {
            Element jobElement = document.createElement("job");

            Element depCode = document.createElement("depCode");
            depCode.setTextContent(job.getDepCode());

            Element depJob = document.createElement("depJob");
            depJob.setTextContent(job.getDepJob());

            jobElement.appendChild(depCode);
            jobElement.appendChild(depJob);
            if(job.getDescription() != null) {
                Element description = document.createElement("description");
                description.setTextContent(job.getDescription());
                jobElement.appendChild(description);
            }

            root.appendChild(jobElement);
        }

        log.info("Xml document created.");

        return document;
    }
}