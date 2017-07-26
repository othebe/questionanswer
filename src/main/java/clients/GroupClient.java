package clients;

import clients.dto.FeedDto;
import common.CouchDBClient;
import common.JSONMapper;
import models.Message;
import models.MessageThread;
import models.User;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static common.Constants.REQUEST_DELAY;

public class GroupClient {
    public static List<MessageThread> getThreads(long groupId, String authBearer) throws Exception {
        String formattedUrl = String.format("https://appsapi.yammer.com/mobile/feeds/v1/group_feed/%d?inbox_supported_client=true&update_last_seen_message_id=true&threaded=extended&include_counts=true&replies_per_thread=2&limit=10", groupId);

        String cached = CouchDBClient.INSTANCE.readData(formattedUrl, String.class);
        if (cached != null) {
            List<Map<String, Object>> messageThreadMaps = JSONMapper.INSTANCE.readValue(cached, List.class);
            List<MessageThread> messageThreads = new ArrayList<MessageThread>();
            for (Map<String, Object> messageThreadMap : messageThreadMaps) {
                messageThreads.add(MessageThread.fromMap(messageThreadMap));
            }
            return messageThreads;
        }

        URL url = new URL(formattedUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authBearer);
        Thread.sleep(REQUEST_DELAY);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        FeedDto feedDto = JSONMapper.INSTANCE.readValue(stringBuilder.toString(), FeedDto.class);
        List<MessageThread> messageThreads = MessageThread.fromGroupFeedDto(feedDto);

        if (!messageThreads.isEmpty()) {
            List<MessageThread> additionalThreads = null;
            int maxDepth = 10;
            int currDepth = 0;
            do {
                long olderThan = additionalThreads == null ?
                        messageThreads.get(messageThreads.size() - 1).threadStarter.id :
                        additionalThreads.get(additionalThreads.size() - 1).threadStarter.id;

                additionalThreads = getThreadsOlderThan(groupId, olderThan, authBearer);

                List<MessageThread> mergedList = new ArrayList<MessageThread>();
                mergedList.addAll(additionalThreads);
                mergedList.addAll(messageThreads);

                messageThreads = mergedList;
                currDepth++;
                System.out.printf("     %d\n", currDepth);
            } while (additionalThreads != null && !additionalThreads.isEmpty() && currDepth < maxDepth);
        }

        // Populate threads.
        for (MessageThread messageThread : messageThreads) {
            // Add replies.
            List<Message> replies = new ArrayList<Message>();
            try {
                replies = ThreadClient.getMessages(messageThread.threadStarter.id, authBearer);
                List<Message> ordered = new ArrayList<Message>();
                for (int i = replies.size() - 1; i >= 0; i--) {
                    ordered.add(replies.get(i));
                }
                replies = ordered;
            } catch (Exception e) {
                System.out.print(e.toString());
            }
            messageThread.replies = replies;

            // User.
            User user = UserClient.getUser(messageThread.threadStarter.sender_id, authBearer);
            messageThread.threadStarter.user = user;
        }

        CouchDBClient.INSTANCE.saveData(formattedUrl, JSONMapper.INSTANCE.writeValueAsString(messageThreads));

        return messageThreads;
    }

    private static List<MessageThread> getThreadsOlderThan(long groupId, long olderThan, String authBearer) throws Exception {
        String formattedUrl = String.format("https://appsapi.yammer.com/mobile/feeds/v1/group_feed/%d?inbox_supported_client=true&update_last_seen_message_id=true&threaded=extended&older_than=%d&include_counts=true&replies_per_thread=2&limit=10", groupId, olderThan);

        URL url = new URL(formattedUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authBearer);
        Thread.sleep(REQUEST_DELAY);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return MessageThread.fromGroupFeedDto(JSONMapper.INSTANCE.readValue(stringBuilder.toString(), FeedDto.class));
    }
}
