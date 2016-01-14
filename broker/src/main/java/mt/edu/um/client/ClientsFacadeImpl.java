package mt.edu.um.client;

import mt.edu.um.monitor.ClientMonitorImpl;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by matthew on 13/12/2015.
 */
public class ClientsFacadeImpl implements ClientsFacade {

    private ReentrantReadWriteLock lock;
    private final Map<Integer, Client> clients = new HashMap<>();

    public ClientsFacadeImpl() {
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean create(int id) {
        try {
            lock.writeLock().lock();
            if (clients.containsKey(id)) {
                return false;
            }

            Client client = new Client(id);
            clients.put(id, client);

            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ClientMonitorImpl subscriberMonitor = new ClientMonitorImpl(client);
            try {
                mBeanServer.registerMBean(subscriberMonitor, subscriberMonitor.getObjectName());
            } catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException e) {
                e.printStackTrace();
            }

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(int id) {
        try {
            lock.writeLock().lock();
            if (!clients.containsKey(id)) {
                return false;
            }

            clients.remove(id);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Client> get(int id) {
        try {
            lock.readLock().lock();
            if (!clients.containsKey(id)) {
                return Optional.empty();
            }

            return Optional.of(clients.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }
}
