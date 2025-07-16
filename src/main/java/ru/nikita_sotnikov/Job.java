package ru.nikita_sotnikov;

/**
 * Represents a job entry with details such as ID, department code,
 * department job title, and a description.
 * This class serves as a data model for job records in the database and XML files.
 * <p>
 * The natural key for a Job is the combination of {@code depCode} and {@code depJob}.
 * </p>
 */
public class Job{
    private int id;
    private String depCode;
    private String depJob;
    private String description;

    /**
     * Default constructor for creating a new Job instance.
     */
    public Job() {
    }

    /**
     * Returns the unique identifier of the job.
     *
     * @return The ID of the job.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the job.
     *
     * @param id The ID to set for the job.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the department code for the job.
     *
     * @return The department code.
     */
    public String getDepCode() {
        return depCode;
    }

    /**
     * Sets the department code for the job.
     *
     * @param depCode The department code to set (maximum length 20 characters).
     */
    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    /**
     * Returns the department job title for the job.
     *
     * @return The department job title.
     */
    public String getDepJob() {
        return depJob;
    }

    /**
     * Sets the department job title for the job.
     *
     * @param depJob The department job title to set (maximum length 100 characters).
     */
    public void setDepJob(String depJob) {
        this.depJob = depJob;
    }

    /**
     * Returns the description for the job.
     *
     * @return The description of the job.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for the job.
     *
     * @param description The description to set for the job (maximum length 255 characters).
     */
    public void setDescription(String description) {
        this.description = description;
    }
}