package mt.edu.um.subscriber;

import java.util.Optional;

/**
 * Created by matthew on 13/12/2015.
 */
public interface SubscribersFacade {

    /**
     * Adds a subscriber
     * @param id a unique id for the subscriber
     * @return true on success, false if subscriber with that id already exists
     */
    boolean subscribe(int id);

    /**
     * Removes a subscriber
     * @param id the id used to subscribe
     * @return true on success, false if subscriber is not found
     */
    boolean unsubscribe(int id);

    /**
     * Retrieves a subscriber
     * @param id the id used to subscribe
     * @return a subscriber if found
     */
    Optional<Subscriber> get(int id);

}
