package ru.nikita_sotnikov;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An implementation of {@link RowMapper} for mapping rows from a JDBC {@link ResultSet}
 * to {@link Job} objects.
 */
public class JobMapper implements RowMapper<Job> {
    /**
     * Maps a single row of a {@link ResultSet} to a {@link Job} object.
     *
     * @param rs The {@link ResultSet} to map (pre-initialized to the current row).
     * @param rowNum The number of the current row.
     * @return A fully populated {@link Job} object.
     * @throws SQLException If an error occurs while extracting data from the {@link ResultSet}.
     */
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