/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
	   private static Set<String> extractMentions(String text) {
	        Set<String> mentions = new HashSet<>();
	        String[] words = text.split("\\s+"); // Split the text into words by whitespace

	        // Iterate over the words to find mentions (words starting with '@')
	        for (String word : words) {
	            if (word.startsWith("@")) {
	                String mention = word.substring(1).toLowerCase().replaceAll("[^a-z0-9_]", ""); // Extract username
	                if (!mention.isEmpty()) {
	                    mentions.add(mention);
	                }
	            }
	        }

	        return mentions;
	    }
	   public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
	        Map<String, Set<String>> followsGraph = new HashMap<>();
	        
	        // Iterate through each tweet
	        for (Tweet tweet : tweets) {
	            String author = tweet.getAuthor().toLowerCase(); // Normalize usernames to lowercase
	            Set<String> mentionedUsers = extractMentions(tweet.getText());

	            if(!mentionedUsers.isEmpty()) {
		            followsGraph.putIfAbsent(author, new HashSet<>());
		            followsGraph.get(author).addAll(mentionedUsers); // The author "follows" all mentioned users
	            }
	        }

	        return followsGraph;
	   }

	   public static List<String> influencers(Map<String, Set<String>> followsGraph) {
	        // Map to store follower counts for each user
	        Map<String, Integer> followerCounts = new HashMap<>();
	        
	        // Iterate over each user and their followees in the followsGraph
	        for (String user : followsGraph.keySet()) {
	            for (String followee : followsGraph.get(user)) {
	                // Increment the follower count for each followee
	                followerCounts.put(followee, followerCounts.getOrDefault(followee, 0) + 1);
	            }
	        }
	        
	        // Create a list of all users to sort by follower count
	        List<String> influencers = new ArrayList<>(followerCounts.keySet());
	        
	        // Sort the list in descending order of follower count
	        influencers.sort((user1, user2) -> followerCounts.get(user2) - followerCounts.get(user1));
	        
	        return influencers;
	    }

}
