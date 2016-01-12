package mt.edu.um.subscriber;

import java.time.LocalDateTime;
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
     * Updates the last active time for the subscriber
     * @param id the id of the subscriber
     * @param lastActive the time at which the last activity occured
     * @return true on success, false on failure
     */
    boolean update(int id, LocalDateTime lastActive);

    /**
     * Retrieves a subscriber
     * @param id the id used to subscribe
     * @return a subscriber if found
     */
    Optional<Subscriber> get(int id);

}
