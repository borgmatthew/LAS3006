package mt.edu.um.task;

import mt.edu.um.subscriber.SubscribersFacade;

import java.util.concurrent.Callable;

/**
 * Created by matthew on 11/12/2015.
 */
public class DisconnectTask implements Callable<Boolean> {

    private SubscribersFacade subscribersFacade;
    private int id;

    public DisconnectTask(SubscribersFacade subscribersFacade, int id) {
        this.subscribersFacade = subscribersFacade;
        this.id = id;
    }

    @Override
    public Boolean call() throws Exception {
        return subscribersFacade.unsubscribe(id);
    }
}
