package mt.edu.um.topictree;

import mt.edu.um.client.Client;
import mt.edu.um.topic.Topic;
import mt.edu.um.topic.TopicPath;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static junit.framework.TestCase.*;

/**
 * Created by matthew on 03/12/2015.
 */
public class TopicTreeImplTest {

    private TopicTree topicTree;
    private TopicPath topicPath;
    private Set<Client> clients;

    @Before
    public void setUp() throws Exception {
        topicTree = new TopicTreeImpl();
        clients = new HashSet<>();
    }

    @Test
    public void testInsert_emptyTopicList() throws Exception {
        topicPath = new TopicPath(new ArrayList<>(0));
        clients.add(new Client(1));

        assertFalse(topicTree.insert(topicPath, clients));
    }

    @Test
    public void testInsert_emptySubscriberSet() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("topicOne")));

        assertFalse(topicTree.insert(topicPath, clients));
    }

    @Test
    public void testInsert_duplicateKey() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("topicOne")));
        clients.add(new Client(1));

        //First insert should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        //Second insert should fail
        assertFalse(topicTree.insert(topicPath, clients));
    }

    @Test
    public void testInsert_multipleLevels() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        clients.add(new Client(1));

        assertTrue(topicTree.insert(topicPath, clients));
    }

    @Test
    public void testInsert_multipleLevels_siblings() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("siblingTopic")));
        clients.add(new Client(1));

        assertTrue(topicTree.insert(topicPath, clients));
    }

    @Test
    public void testGet_emptyTopicList() throws Exception {
        topicPath = new TopicPath(new ArrayList<>());

        assertTrue(topicTree.get(topicPath).isEmpty());
    }

    @Test
    public void testGetSubscribers_retrievesSubscribers() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Client client = new Client(1);
        clients.add(client);

        Set<Client> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("siblingTopic")));
        Client clientTwo = new Client(1);
        subscribersTwo.add(clientTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        assertEquals(clients, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testGetSubscribers_retrievesSubscribersWithHashWildCard() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Client client = new Client(1);
        clients.add(client);

        Set<Client> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("#")));
        Client clientTwo = new Client(1);
        subscribersTwo.add(clientTwo);

        Set<Client> result = new HashSet<>(clients);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        assertEquals(result, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testGetSubscribers_retrievesSubscribersWithHashWildCard_multipleLevels() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Client client = new Client(1);
        clients.add(client);

        Set<Client> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("#")));
        Client clientTwo = new Client(1);
        subscribersTwo.add(clientTwo);

        Set<Client> result = new HashSet<>(clients);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        assertEquals(result, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testGetSubscribers_retrievesSubscribersWithPlusWildCard() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        Set<Client> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("+"), new Topic("grandChild")));
        Client clientTwo = new Client(1);
        subscribersTwo.add(clientTwo);

        Set<Client> result = new HashSet<>(clients);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));
        assertEquals(result, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testGetSubscribers_plusWildCard_noMatch() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        Set<Client> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("+"), new Topic("childTopic")));
        Client clientTwo = new Client(1);
        subscribersTwo.add(clientTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        assertEquals(clients, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testTraverse_visitsEveryNode() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        Set<Client> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("+"), new Topic("childTopic")));
        Client clientTwo = new Client(1);
        subscribersTwo.add(clientTwo);

        Consumer consumer = Mockito.mock(Consumer.class);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        topicTree.traverse(consumer);

        Mockito.verify(consumer, Mockito.times(5)).accept(Mockito.any());
        Mockito.verifyNoMoreInteractions(consumer);
    }

    @Test
    public void testRemove_oneNodeIsRemoved_parentContainsMoreChildren() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        Set<Client> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("funTopic")));
        Client clientTwo = new Client(1);
        subscribersTwo.add(clientTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        //Remove is successful
        assertTrue(topicTree.remove(topicPath));

        //Check that the clients for the topic "parentTopic/childTopic/funTopic" is still in the tree
        assertEquals(subscribersTwo, topicTree.getSubscribers(topicPathTwo));
    }

    @Test
    public void testRemove_emptyNodesAreRemoved() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, clients));

        //Remove is successful
        assertTrue(topicTree.remove(topicPath));

        //Check that tree is empty
        assertEquals(0, topicTree.size());
    }

    @Test
    public void testRemove_nodeWithNoChildren_containsSubscribers_notRemoved() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        this.clients.add(client);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, this.clients));

        //Add clients to /parentTopic/childTopic
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Set<Client> clients = topicTree.get(topicPathTwo);
        clients.add(new Client(1));

        //Remove is successful
        assertTrue(topicTree.remove(topicPath));

        assertEquals(2, topicTree.size());
    }

    @Test
    public void testSize_multipleNodes() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("funTopic")));

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, clients));
        assertTrue(topicTree.insert(topicPathTwo, clients));

        assertEquals(4, topicTree.size());
    }

    @Test
    public void testSize_rootNodeShouldNotBeCounted() throws Exception {
        assertEquals(0, topicTree.size());
    }

    @Test
    public void testGet_retrievesSameObject() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, clients));

        assertEquals(clients, topicTree.get(topicPath));
    }

    @Test
    public void testGet_emptyPath_emptySetIsReturned() throws Exception {
        assertEquals(0, topicTree.get(new TopicPath(new ArrayList<>(0))).size());
    }

    @Test
    public void testGet_keyNotFound_emptySetIsReturned() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, clients));

        assertEquals(0, topicTree.get(new TopicPath(Arrays.asList(new Topic("testTopic")))).size());
    }

    @Test
    public void testContains_returnsTrueWhenObjectFound() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, clients));

        assertTrue(topicTree.contains(topicPath));
    }

    @Test
    public void testContains_emptyPath_falseIsReturned() throws Exception {
        assertFalse(topicTree.contains(new TopicPath(new ArrayList<>(0))));
    }

    @Test
    public void testContains_keyNotFound_falseIsReturned() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Client client = new Client(1);
        clients.add(client);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, clients));

        assertFalse(topicTree.contains(new TopicPath(Arrays.asList(new Topic("TestTopic")))));
    }
}