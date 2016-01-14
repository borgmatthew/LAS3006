package mt.edu.um.monitor;

import mt.edu.um.client.Client;
import mt.edu.um.topic.TopicPath;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by matthew on 14/01/2016.
 */
public class ClientMonitorImpl implements ClientMonitor {

    private final Client client;

    public ClientMonitorImpl(Client client) {
        this.client = client;
    }

    @Override
    public int getId() {
        return client.getId();
    }

    @Override
    public int getPublishedMessagesCount() {
        return client.getPublishedMessages().get();
    }

    @Override
    public int getReceivedMessagesCount() {
        return client.getReceivedMessages().get();
    }

    @Override
    public List<String> getSubscribedTopics() {
        return client.getTopics().stream().map(TopicPath::toString).collect(Collectors.toList());
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("mt.edu.um.client:type=Client,name=client#" + client.getId());
    }
}
