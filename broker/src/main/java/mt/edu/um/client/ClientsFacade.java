package mt.edu.um.client;

import java.util.Optional;

/**
 * Created by matthew on 13/12/2015.
 */
public interface ClientsFacade {

    /**
     * Adds a subscriber
     * @param id a unique id for the subscriber
     * @return true on success, false if subscriber with that id already exists
     */
    boolean create(int id);

    /**
     * Removes a subscriber
     * @param id the id used to create
     * @return true on success, false if subscriber is not found
     */
    boolean remove(int id);

    /**
     * Retrieves a subscriber
     * @param id the id used to create
     * @return a subscriber if found
     */
    Optional<Client> get(int id);

}
