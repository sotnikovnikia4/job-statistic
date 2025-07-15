package ru.nikita_sotnikov;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JobMapper implements RowMapper<Job> {
    @Override
    public Job mapRow(ResultSet rs, int rowNum) throws SQLException {
        Job job = new Job();
        job.setId(rs.getInt("id"));
        job.setDepJob(rs.getString("dep_job"));
        job.setDepCode(rs.getString("dep_code"));
        job.setDescription(rs.getString("description"));
        return job;
    }
}
