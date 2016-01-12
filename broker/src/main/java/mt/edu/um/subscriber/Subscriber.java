package mt.edu.um.subscriber;

/**
 * Created by matthew on 26/11/2015.
 */
public class Subscriber {

    private int id;

    public Subscriber(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
