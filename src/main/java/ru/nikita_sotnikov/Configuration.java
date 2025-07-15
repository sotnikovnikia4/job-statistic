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

public class Configuration {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private Properties properties;
    private DBOperations dbOperations;
    private TransactionTemplate transactionTemplate;

    private XmlParser xmlParser;

    private SyncService syncService;
    private SaveService saveService;

    private Configuration(){}

    public static Configuration create(String propertiesFile){
        Configuration config = new Configuration();
        config.properties = loadPropertiesFromClasspath(propertiesFile);

        return config;
    }

    private static Properties loadPropertiesFromClasspath(String fileName) {
        Properties properties = new Properties();
        // Используем ClassLoader для получения InputStream к ресурсу
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new FileNotFoundException("Файл не найден");
            }
            properties.load(input);
        } catch (IOException ex) {
            System.err.println("Ошибка при загрузке настроек из файла" + ex.getMessage());
        }

        return properties;
    }

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

    public JdbcTemplate getJdbcTemplate() {
        if(jdbcTemplate == null){
            jdbcTemplate = new JdbcTemplate(getDataSource());
        }
        return jdbcTemplate;
    }

    public TransactionTemplate getTransactionTemplate(){
        if(transactionTemplate == null){
            transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(getDataSource()));
        }

        return transactionTemplate;
    }

    public DBOperations getDbOperations(){
        if(dbOperations == null){
            dbOperations = new DBOperations(getJdbcTemplate(), getTransactionTemplate());
        }

        return dbOperations;
    }

    public SaveService getSaveService() {
        if(saveService == null){
            saveService = new SaveService(getDbOperations(), getXmlParser());
        }

        return saveService;
    }

    public SyncService getSyncService() {
        if(syncService == null){
            syncService = new SyncService(getXmlParser(), getDbOperations());
        }

        return syncService;
    }

    private XmlParser getXmlParser() {
        if(xmlParser == null){
            xmlParser = new XmlParser();
        }
        return xmlParser;
    }
}
