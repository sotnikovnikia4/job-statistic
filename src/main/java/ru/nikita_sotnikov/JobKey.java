package ru.nikita_sotnikov;

import java.util.Objects;

public record JobKey(String depCode, String depJob) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobKey jobKey = (JobKey) o;
        return Objects.equals(depCode(), jobKey.depCode()) && Objects.equals(depJob(), jobKey.depJob());
    }

    @Override
    public int hashCode() {
        return Objects.hash(depCode(), depJob());
    }
}
