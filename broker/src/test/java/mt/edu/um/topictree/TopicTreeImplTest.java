package mt.edu.um.topictree;

import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.Topic;
import mt.edu.um.topic.TopicPath;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by matthew on 03/12/2015.
 */
public class TopicTreeImplTest {

    private TopicTree topicTree;
    private TopicPath topicPath;
    private Set<Subscriber> subscribers;

    @Before
    public void setUp() throws Exception {
        topicTree = new TopicTreeImpl();
        subscribers = new HashSet<>();
    }

    @Test
    public void testInsert_emptyTopicList() throws Exception {
        topicPath = new TopicPath(new ArrayList<>(0));
        subscribers.add(new Subscriber(1, LocalDateTime.now()));

        assertFalse(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testInsert_emptySubscriberSet() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("topicOne")));

        assertFalse(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testInsert_duplicateKey() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("topicOne")));
        subscribers.add(new Subscriber(1, LocalDateTime.now()));

        //First insert should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        //Second insert should fail
        assertFalse(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testInsert_multipleLevels() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        subscribers.add(new Subscriber(1, LocalDateTime.now()));

        assertTrue(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testInsert_multipleLevels_siblings() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("siblingTopic")));
        subscribers.add(new Subscriber(1, LocalDateTime.now()));

        assertTrue(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testGet_emptyTopicList() throws Exception {
        topicPath = new TopicPath(new ArrayList<>());

        assertTrue(topicTree.get(topicPath).isEmpty());
    }

    @Test
    public void testGetSubscribers_retrievesSubscribers() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("siblingTopic")));
        Subscriber subscriberTwo = new Subscriber(1, LocalDateTime.now());
        subscribersTwo.add(subscriberTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        assertEquals(subscribers, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testGetSubscribers_retrievesSubscribersWithHashWildCard() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("#")));
        Subscriber subscriberTwo = new Subscriber(1, LocalDateTime.now());
        subscribersTwo.add(subscriberTwo);

        Set<Subscriber> result = new HashSet<>(subscribers);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        assertEquals(result, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testGetSubscribers_retrievesSubscribersWithHashWildCard_multipleLevels() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("#")));
        Subscriber subscriberTwo = new Subscriber(1, LocalDateTime.now());
        subscribersTwo.add(subscriberTwo);

        Set<Subscriber> result = new HashSet<>(subscribers);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        assertEquals(result, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testGetSubscribers_retrievesSubscribersWithPlusWildCard() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("+"), new Topic("grandChild")));
        Subscriber subscriberTwo = new Subscriber(1, LocalDateTime.now());
        subscribersTwo.add(subscriberTwo);

        Set<Subscriber> result = new HashSet<>(subscribers);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));
        assertEquals(result, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testGetSubscribers_plusWildCard_noMatch() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("+"), new Topic("childTopic")));
        Subscriber subscriberTwo = new Subscriber(1, LocalDateTime.now());
        subscribersTwo.add(subscriberTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        assertEquals(subscribers, topicTree.getSubscribers(topicPath));
    }

    @Test
    public void testTraverse_visitsEveryNode() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("+"), new Topic("childTopic")));
        Subscriber subscriberTwo = new Subscriber(1, LocalDateTime.now());
        subscribersTwo.add(subscriberTwo);

        Consumer consumer = Mockito.mock(Consumer.class);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        topicTree.traverse(consumer);

        Mockito.verify(consumer, Mockito.times(5)).accept(Mockito.any());
        Mockito.verifyNoMoreInteractions(consumer);
    }

    @Test
    public void testRemove_oneNodeIsRemoved_parentContainsMoreChildren() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("funTopic")));
        Subscriber subscriberTwo = new Subscriber(1, LocalDateTime.now());
        subscribersTwo.add(subscriberTwo);

        //Inserts should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));

        //Remove is successful
        assertTrue(topicTree.remove(topicPath));

        //Check that the subscribers for the topic "parentTopic/childTopic/funTopic" is still in the tree
        assertEquals(subscribersTwo, topicTree.getSubscribers(topicPathTwo));
    }

    @Test
    public void testRemove_emptyNodesAreRemoved() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));

        //Remove is successful
        assertTrue(topicTree.remove(topicPath));

        //Check that tree is empty
        assertEquals(0, topicTree.size());
    }

    @Test
    public void testRemove_nodeWithNoChildren_containsSubscribers_notRemoved() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));

        //Add subscribers to /parentTopic/childTopic
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Set<Subscriber> subscribers = topicTree.get(topicPathTwo);
        subscribers.add(new Subscriber(1, LocalDateTime.now()));

        //Remove is successful
        assertTrue(topicTree.remove(topicPath));

        assertEquals(2, topicTree.size());
    }

    @Test
    public void testSize_multipleNodes() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("funTopic")));

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));
        assertTrue(topicTree.insert(topicPathTwo, subscribers));

        assertEquals(4, topicTree.size());
    }

    @Test
    public void testSize_rootNodeShouldNotBeCounted() throws Exception {
        assertEquals(0, topicTree.size());
    }

    @Test
    public void testGet_retrievesSameObject() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));

        assertEquals(subscribers, topicTree.get(topicPath));
    }

    @Test
    public void testGet_emptyPath_emptySetIsReturned() throws Exception {
        assertEquals(0, topicTree.get(new TopicPath(new ArrayList<>(0))).size());
    }

    @Test
    public void testGet_keyNotFound_emptySetIsReturned() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));

        assertEquals(0, topicTree.get(new TopicPath(Arrays.asList(new Topic("testTopic")))).size());
    }

    @Test
    public void testContains_returnsTrueWhenObjectFound() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));

        assertTrue(topicTree.contains(topicPath));
    }

    @Test
    public void testContains_emptyPath_falseIsReturned() throws Exception {
        assertFalse(topicTree.contains(new TopicPath(new ArrayList<>(0))));
    }

    @Test
    public void testContains_keyNotFound_falseIsReturned() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber(1, LocalDateTime.now());
        subscribers.add(subscriber);

        //Insert should succeed
        assertTrue(topicTree.insert(topicPath, subscribers));

        assertFalse(topicTree.contains(new TopicPath(Arrays.asList(new Topic("TestTopic")))));
    }
}