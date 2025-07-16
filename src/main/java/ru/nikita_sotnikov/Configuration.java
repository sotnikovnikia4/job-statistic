package ru.nikita_sotnikov;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration class for the application.
 * This class is responsible for loading application properties from a file,
 * and providing configured instances of various services and components
 * such as {@link DataSource}, {@link JdbcTemplate}, {@link TransactionTemplate},
 * {@link DBOperations}, {@link XmlParser}, {@link SyncService}, and {@link SaveService}.
 */
public class Configuration {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private Properties properties;
    private DBOperations dbOperations;
    private TransactionTemplate transactionTemplate;

    private XmlParser xmlParser;

    private SyncService syncService;
    private SaveService saveService;

    /**
     * Private constructor to prevent direct instantiation.
     * Use the static {@link #create(String)} method to get a configured instance.
     */
    private Configuration(){}

    /**
     * Creates and initializes a new {@link Configuration} instance by loading properties
     * from the specified properties file.
     *
     * @param propertiesFile The name of the properties file to load from the classpath.
     * @return A new, configured {@link Configuration} instance.
     */
    public static Configuration create(String propertiesFile){
        Configuration config = new Configuration();
        config.properties = loadPropertiesFromClasspath(propertiesFile);

        return config;
    }

    /**
     * Loads properties from a specified file located in the classpath.
     *
     * @param fileName The name of the properties file.
     * @return A {@link Properties} object containing the loaded key-value pairs.
     * @throws RuntimeException if the file is not found or an I/O error occurs during loading.
     */
    private static Properties loadPropertiesFromClasspath(String fileName) {
        Properties properties = new Properties();
        // Используем ClassLoader для получения InputStream к ресурсу
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new FileNotFoundException("File not found");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load properties from file: " + fileName, ex);
        }

        return properties;
    }

    /**
     * Returns a singleton instance of {@link DataSource}.
     * If the instance does not exist, it is created using properties loaded from the configuration file.
     *
     * @return The configured {@link DataSource} instance.
     */
    public DataSource getDataSource() {
        if(dataSource == null){
            DriverManagerDataSource dataSource = new DriverManagerDataSource();

            dataSource.setDriverClassName(properties.getProperty("dataSource.driverClassName"));
            dataSource.setUrl(properties.getProperty("dataSource.url"));
            dataSource.setUsername(properties.getProperty("dataSource.username"));
            dataSource.setPassword(properties.getProperty("dataSource.password"));

            this.dataSource = dataSource;
        }

        return dataSource;
    }

    /**
     * Returns a singleton instance of {@link JdbcTemplate}.
     * If the instance does not exist, it is created using the configured {@link DataSource}.
     *
     * @return The configured {@link JdbcTemplate} instance.
     */
    public JdbcTemplate getJdbcTemplate() {
        if(jdbcTemplate == null){
            jdbcTemplate = new JdbcTemplate(getDataSource());
        }
        return jdbcTemplate;
    }

    /**
     * Returns a singleton instance of {@link TransactionTemplate}.
     * If the instance does not exist, it is created using a {@link DataSourceTransactionManager}
     * with the configured {@link DataSource}.
     *
     * @return The configured {@link TransactionTemplate} instance.
     */
    public TransactionTemplate getTransactionTemplate(){
        if(transactionTemplate == null){
            transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(getDataSource()));
        }

        return transactionTemplate;
    }

    /**
     * Returns a singleton instance of {@link DBOperations}.
     * If the instance does not exist, it is created using the configured {@link JdbcTemplate}
     * and {@link TransactionTemplate}.
     *
     * @return The configured {@link DBOperations} instance.
     */
    public DBOperations getDbOperations(){
        if(dbOperations == null){
            dbOperations = new DBOperations(getJdbcTemplate(), getTransactionTemplate());
        }

        return dbOperations;
    }

    /**
     * Returns a singleton instance of {@link SaveService}.
     * If the instance does not exist, it is created using the configured {@link DBOperations}
     * and {@link XmlParser}.
     *
     * @return The configured {@link SaveService} instance.
     */
    public SaveService getSaveService() {
        if(saveService == null){
            saveService = new SaveService(getDbOperations(), getXmlParser());
        }

        return saveService;
    }

    /**
     * Returns a singleton instance of {@link SyncService}.
     * If the instance does not exist, it is created using the configured {@link XmlParser}
     * and {@link DBOperations}.
     *
     * @return The configured {@link SyncService} instance.
     */
    public SyncService getSyncService() {
        if(syncService == null){
            syncService = new SyncService(getXmlParser(), getDbOperations());
        }

        return syncService;
    }

    /**
     * Returns a singleton instance of {@link XmlParser}.
     * If the instance does not exist, it is created.
     *
     * @return The configured {@link XmlParser} instance.
     */
    private XmlParser getXmlParser() {
        if(xmlParser == null){
            xmlParser = new XmlParser();
        }
        return xmlParser;
    }
}