package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing strategy for guessFollowsGraph:
     * 
     * Partitions:
     * - Empty list of tweets
     * - Tweets with no mentions
     * - Tweets with a single mention
     * - Tweets with multiple mentions
     * - Multiple tweets from the same user with repeated or new mentions
     */

 

    // 1. Empty List of Tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    // 2. Tweets Without Mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Tweet tweet1 = new Tweet(1, "user1", "This is a tweet with no mentions", Instant.now());
        Tweet tweet2 = new Tweet(2, "user2", "Another tweet without mentions", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet1, tweet2);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected empty follow graph for tweets with no mentions", followsGraph.isEmpty());
    }

    // 3. Single Mention
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Tweet tweet = new Tweet(1, "user1", "Hello @user2!", Instant.now());
        List<Tweet> tweets = Collections.singletonList(tweet);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("user1 should follow user2", followsGraph.containsKey("user1"));
        assertTrue("user1 should follow user2", followsGraph.get("user1").contains("user2"));
    }

    // 4. Multiple Mentions
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Tweet tweet = new Tweet(1, "user1", "Hello @user2 and @user3!", Instant.now());
        List<Tweet> tweets = Collections.singletonList(tweet);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("user1 should follow user2 and user3", followsGraph.containsKey("user1"));
        assertTrue("user1 should follow user2", followsGraph.get("user1").contains("user2"));
        assertTrue("user1 should follow user3", followsGraph.get("user1").contains("user3"));
    }

    // 5. Multiple Tweets from One User
    @Test
    public void testGuessFollowsGraphMultipleTweetsSameUser() {
        Tweet tweet1 = new Tweet(1, "user1", "First tweet mentioning @user2", Instant.now());
        Tweet tweet2 = new Tweet(2, "user1", "Second tweet mentioning @user3", Instant.now());
        List<Tweet> tweets = Arrays.asList(tweet1, tweet2);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("user1 should follow user2 and user3", followsGraph.containsKey("user1"));
        assertTrue("user1 should follow user2", followsGraph.get("user1").contains("user2"));
        assertTrue("user1 should follow user3", followsGraph.get("user1").contains("user3"));
    }
    
    // Test 6: Empty Graph for influencers
    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list of influencers", influencers.isEmpty());
    }
    
    // Test 7: Single User Without Followers
    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>()); // user1 follows nobody
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list of influencers", influencers.isEmpty());
    }
    
    // Test 8: Single Influencer
    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>(Arrays.asList("user2"))); // user1 follows user2
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected user2 to be the influencer", Arrays.asList("user2"), influencers);
    }
    
    // Test 9: Multiple Influencers
    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>(Arrays.asList("user2", "user3")));
        followsGraph.put("user2", new HashSet<>(Arrays.asList("user3")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        // user3 should come first because they are followed by both user1 and user2 (2 followers),
        // followed by user2 with 1 follower (user1)
        assertEquals("expected user3 to be the top influencer", Arrays.asList("user3", "user2"), influencers);
    }
    
    // Test 10: Tied Influence
    @Test
    public void testInfluencersTiedInfluence() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", new HashSet<>(Arrays.asList("user2", "user3")));
        followsGraph.put("user2", new HashSet<>(Arrays.asList("user3")));
        followsGraph.put("user3", new HashSet<>(Arrays.asList("user2")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        // user2 and user3 are both followed by 2 users each, so their order can vary
        List<String> expected1 = Arrays.asList("user3", "user2");
        List<String> expected2 = Arrays.asList("user2", "user3");
        
        assertTrue("expected user2 and user3 to be tied influencers", 
            influencers.equals(expected1) || influencers.equals(expected2));
    }

}

