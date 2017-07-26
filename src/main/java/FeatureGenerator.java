import common.CouchDBClient;
import models.Group;
import models.Message;
import models.MessageThread;
import models.User;

import java.util.*;

import static common.Constants.ANSWERS_DB_NAME;

public class FeatureGenerator {
    public static final int NUM_FEATURES = 7;

    public static void main(String[] args) {
        FeatureGenerator featureGenerator = new FeatureGenerator();
        Set<List<List<Double>>> scoredFeaturesByThread = featureGenerator.getScoredFeaturesByThread();

        int x = 1;
    }

    private static final double NORMALIZER = 10.0f;

    private CouchDBClient dbClient;

    public FeatureGenerator() {
        dbClient = new CouchDBClient(ANSWERS_DB_NAME);
    }

    public Set<List<List<Double>>> getScoredFeaturesByThread() {
        Set<List<List<Double>>> scoredFeaturesByThread = new HashSet<List<List<Double>>>();

        for (String threadId : dbClient.db.getAllDocIds()) {
            Map<String, Object> entry = dbClient.readData(threadId, Map.class);
            MessageThread thread = MessageThread.fromMap((Map) entry.get("thread"));
            List<String> answers = (List) entry.get("answers");

            List<List<Double>> features = getScoredFeaturesForThreadCollection(thread, answers);
            scoredFeaturesByThread.add(features);
        }

        return scoredFeaturesByThread;
    }

    private List<List<Double>> getScoredFeaturesForThreadCollection(MessageThread thread, List<String> answers) {
        Map<Long, List<Double>> messageFeatureMap = new HashMap<Long, List<Double>>();
        for (int i = 0; i < thread.replies.size(); i++) {
            Message reply = thread.replies.get(i);
            List<Double> features = getFeatures(reply, thread, i);
            messageFeatureMap.put(reply.id, features);
        }

        List<List<Double>> scoredFeaturesList = new ArrayList<List<Double>>();

        // Get answers.
        for (int answerNdx = 0; answerNdx < answers.size(); answerNdx++) {
            long answerId = thread.replies.get(Integer.valueOf(answers.get(answerNdx))).id;
            List<Double> scoredFeatures = messageFeatureMap.get(answerId);

            // Score.
            scoredFeatures.add((double) (answers.size() - answerNdx));

            scoredFeaturesList.add(scoredFeatures);
            messageFeatureMap.remove(answerId);
        }

        // Get non-answers.
        for (List<Double> scoreFeatures : messageFeatureMap.values()) {
            scoredFeaturesList.add(scoreFeatures);
        }

        return scoredFeaturesList;
    }

    private static List<Double> getFeatures(Message message, MessageThread thread, int position) {
        List<Double> features = new ArrayList<Double>();

        // Normalized like count.
        features.add(Math.tanh(message.likesCount / NORMALIZER));

        long totalFollowing = 0;
        long totalFollowers = 0;
        long totalUpdates = 0;
        for (User user : message.likedBy) {
            totalFollowing += user.following;
            totalFollowers += user.followers;
            totalUpdates += user.updates;
        }
        // Normalized following.
        features.add(Math.tanh(totalFollowing / NORMALIZER));

        // Normalized followers.
        features.add(Math.tanh(totalFollowers / NORMALIZER));

        // Normalized updates;
        features.add(Math.tanh(totalUpdates / NORMALIZER));

        // Is replier different from the poster?
        features.add((double) (message.sender_id == thread.threadStarter.sender_id ? 0 : 1));

        // Position.
        int numReplies = thread.replies.size();
        features.add((double) ((numReplies - position) * 1.0f / thread.replies.size()));

        // Is message in replier's top groups?
        double isInTopGroup = 0f;
        long groupId = thread.threadStarter.groupId;
        for (Group group : message.user.topGroups) {
            if (group.id == groupId) {
                isInTopGroup = 1.0f;
            }
        }
        features.add(isInTopGroup);

        return features;
    }
}
