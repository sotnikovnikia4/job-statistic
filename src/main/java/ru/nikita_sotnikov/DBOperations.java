package ru.nikita_sotnikov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBOperations {
    private static final Logger log = LoggerFactory.getLogger(DBOperations.class);
    private final String TABLE_NAME = "jobs";

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public DBOperations(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    public void insertJobs(List<Job> insertList) {
        jdbcTemplate.batchUpdate("INSERT INTO " + TABLE_NAME + " (dep_code, dep_job, description) VALUES (?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, insertList.get(i).getDepCode());
                ps.setString(2, insertList.get(i).getDepJob());
                ps.setString(3, insertList.get(i).getDescription());
            }

            @Override
            public int getBatchSize() {
                return insertList.size();
            }
        });
    }

    public void updateJobs(List<Job> updateList) {
        jdbcTemplate.batchUpdate("UPDATE " + TABLE_NAME + " SET description=? WHERE id=?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, updateList.get(i).getDescription());
                ps.setInt(2, updateList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return updateList.size();
            }
        });
    }

    public void deleteJobs(List<Job> deleteList) {
        jdbcTemplate.batchUpdate("DELETE FROM " + TABLE_NAME + " WHERE id = ?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, deleteList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return deleteList.size();
            }
        });
    }

    public Map<JobKey, Job> getJobMap(){
        List<Job> result = getJobList();

        Map<JobKey, Job> jobsFromDB = new HashMap<>();

        for(Job job : result){
            jobsFromDB.put(new JobKey(job.getDepCode(), job.getDepJob()), job);
        }

        return jobsFromDB;
    }

    public List<Job> getJobList(){
        List<Job> jobs = jdbcTemplate.query("SELECT * FROM " + TABLE_NAME, new JobMapper());

        log.info("Loaded {} jobs from database", jobs.size());

        return jobs;
    }

    public void refreshDB(List<Job> insertList, List<Job> updateList, List<Job> deleteList) {
        log.info("Start transaction");

        try{
            transactionTemplate.execute(_ -> {
                insertJobs(insertList);
                updateJobs(updateList);
                deleteJobs(deleteList);

                return null;
            });

            log.info("Transaction successful");
        }
        catch(TransactionException e){
            log.error("Transaction error.", e);
        }
    }
}
