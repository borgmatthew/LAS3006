package mt.edu.um.task;

import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.subscriber.SubscribersFacade;

import java.util.concurrent.Callable;

/**
 * Created by matthew on 13/12/2015.
 */
public class PingTask implements Callable<Void> {

    private SubscribersFacade subscribersFacade;
    private Subscriber subscriber;

    @Override
    public Void call() throws Exception {
        return null;
    }
}
