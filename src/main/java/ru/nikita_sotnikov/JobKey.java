package ru.nikita_sotnikov;

import java.util.Objects;

/**
 * A record class representing the natural key for a {@link Job} object.
 * This key is composed of `depCode` and `depJob`.
 * This record automatically provides implementations for `equals()`, `hashCode()`, and `toString()`.
 */
public record JobKey(String depCode, String depJob) {

    /**
     * Indicates whether some other object is "equal to" this one.
     * The comparison is based on the `depCode` and `depJob` fields.
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobKey jobKey = (JobKey) o;
        return Objects.equals(depCode(), jobKey.depCode()) && Objects.equals(depJob(), jobKey.depJob());
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables.
     * The hash code is based on the `depCode` and `depJob` fields.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(depCode(), depJob());
    }
}