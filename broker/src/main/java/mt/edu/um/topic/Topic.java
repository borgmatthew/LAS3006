package mt.edu.um.topic;

/**
 * Created by matthew on 26/11/2015.
 */
public class Topic {

    private String name;

    public Topic(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topic topic = (Topic) o;

        return !(name != null ? !name.equals(topic.name) : topic.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
