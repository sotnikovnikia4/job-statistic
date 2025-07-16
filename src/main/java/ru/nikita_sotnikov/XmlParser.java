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
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParser {
    private static final Logger log = LoggerFactory.getLogger(XmlParser.class);

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
                checkJob(job);

                JobKey jobKey = new JobKey(job.getDepCode(), job.getDepJob());

                if(jobs.containsKey(jobKey)) {
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

    private void checkJob(Job job) throws Exception{
        if(job.getDepCode() == null || job.getDepJob() == null) {
            throw new SAXException("Invalid format: depCode='null' or depJob='null'.");
        }
        else if(job.getDepCode().length() > 20){
            throw new SAXException(String.format("Invalid format: depCode.length > 20, depCode='%s'.", job.getDepCode()));
        }
        else if(job.getDepCode().length() > 100){
            throw new SAXException(String.format("Invalid format: depJob.length > 100, depJob='%s'.", job.getDepJob()));
        }
        else if(job.getDescription() != null && job.getDescription().length() > 255){
            throw new SAXException(String.format("Invalid format: description.length > 100, description='%s'.", job.getDescription()));
        }
    }

    public Document saveToDocument(List<Job> jobs) throws Exception{
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
            if(job.getDepCode() != null) {
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
