package mt.edu.um.topic;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        subscribers.add(new Subscriber(""));

        Assert.assertFalse(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testInsert_emptySubscriberSet() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("topicOne")));

        Assert.assertFalse(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testInsert_duplicateKey() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("topicOne")));
        subscribers.add(new Subscriber(""));

        //First insert should succeed
        Assert.assertTrue(topicTree.insert(topicPath, subscribers));
        //Second insert should fail
        Assert.assertFalse(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testInsert_multipleLevels() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        subscribers.add(new Subscriber(""));

        Assert.assertTrue(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testInsert_multipleLevels_siblings() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("siblingTopic")));
        subscribers.add(new Subscriber(""));

        Assert.assertTrue(topicTree.insert(topicPath, subscribers));
    }

    @Test
    public void testGet_emptyTopicList() throws Exception {
        topicPath = new TopicPath(new ArrayList<>());

        Assert.assertTrue(topicTree.get(topicPath).isEmpty());
    }

    @Test
    public void testGet_retrievesSubscribers() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Subscriber subscriber = new Subscriber("subscriber");
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("siblingTopic")));
        Subscriber subscriberTwo = new Subscriber("subscriberTwo");
        subscribersTwo.add(subscriberTwo);

        //Inserts should succeed
        Assert.assertTrue(topicTree.insert(topicPath, subscribers));
        Assert.assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));
        Assert.assertEquals(subscribers, topicTree.get(topicPath));
    }

    @Test
    public void testGet_retrievesSubscribersWithHashWildCard() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Subscriber subscriber = new Subscriber("subscriber");
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("#")));
        Subscriber subscriberTwo = new Subscriber("subscriberTwo");
        subscribersTwo.add(subscriberTwo);

        Set<Subscriber> result = new HashSet<>(subscribers);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        Assert.assertTrue(topicTree.insert(topicPath, subscribers));
        Assert.assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));
        Assert.assertEquals(result, topicTree.get(topicPath));
    }

    @Test
    public void testGet_retrievesSubscribersWithHashWildCard_multipleLevels() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic")));
        Subscriber subscriber = new Subscriber("subscriber");
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("#")));
        Subscriber subscriberTwo = new Subscriber("subscriberTwo");
        subscribersTwo.add(subscriberTwo);

        Set<Subscriber> result = new HashSet<>(subscribers);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        Assert.assertTrue(topicTree.insert(topicPath, subscribers));
        Assert.assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));
        Assert.assertEquals(result, topicTree.get(topicPath));
    }

    @Test
    public void testGet_retrievesSubscribersWithPlusWildCard() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber("subscriber");
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("+"), new Topic("grandChild")));
        Subscriber subscriberTwo = new Subscriber("subscriberTwo");
        subscribersTwo.add(subscriberTwo);

        Set<Subscriber> result = new HashSet<>(subscribers);
        result.addAll(subscribersTwo);

        //Inserts should succeed
        Assert.assertTrue(topicTree.insert(topicPath, subscribers));
        Assert.assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));
        Assert.assertEquals(result, topicTree.get(topicPath));
    }

    @Test
    public void testGet_plusWildCard_noMatch() throws Exception {
        topicPath = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("childTopic"), new Topic("grandChild")));
        Subscriber subscriber = new Subscriber("subscriber");
        subscribers.add(subscriber);

        Set<Subscriber> subscribersTwo = new HashSet<>();
        TopicPath topicPathTwo = new TopicPath(Arrays.asList(new Topic("parentTopic"), new Topic("+"), new Topic("childTopic")));
        Subscriber subscriberTwo = new Subscriber("subscriberTwo");
        subscribersTwo.add(subscriberTwo);

        //Inserts should succeed
        Assert.assertTrue(topicTree.insert(topicPath, subscribers));
        Assert.assertTrue(topicTree.insert(topicPathTwo, subscribersTwo));
        Assert.assertEquals(subscribers, topicTree.get(topicPath));
    }
}