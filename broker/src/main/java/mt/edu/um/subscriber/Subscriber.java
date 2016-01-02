package mt.edu.um.subscriber;

import java.time.LocalDateTime;

/**
 * Created by matthew on 26/11/2015.
 */
public class Subscriber {

    private int id;
    private LocalDateTime lastActivityTime;

    public Subscriber(int id, LocalDateTime lastActivityTime) {
        this.id = id;
        this.lastActivityTime = lastActivityTime;
    }

    public int getId() {
        return id;
    }

    protected LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }

    public Subscriber setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscriber that = (Subscriber) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
