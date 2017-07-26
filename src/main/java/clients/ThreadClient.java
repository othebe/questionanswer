package clients;

import clients.dto.FeedDto;
import common.CouchDBClient;
import common.JSONMapper;
import models.Message;
import models.User;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static common.Constants.REQUEST_DELAY;

public class ThreadClient {
    public static List<Message> getMessages(long threadId, String authBearer) throws Exception {
        String formattedUrl = String.format("https://appsapi.yammer.com/mobile/feeds/v1/thread/%d?inbox_supported_client=true&threaded=false&include_counts=true&replies_per_thread=10&limit=10", threadId);

        String cached = CouchDBClient.INSTANCE.readData(formattedUrl, String.class);
        if (cached != null) {
            List<Map<String, Object>> messageMaps = JSONMapper.INSTANCE.readValue(cached, List.class);
            List<Message> messages = new ArrayList<Message>();
            for (Map<String, Object> messageMap : messageMaps) {
                messages.add(Message.fromMap(messageMap));
            }
            return messages;
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
        List<Message> messages = Message.fromMessageDtos(feedDto.messages);

        if (!messages.isEmpty()) {
            List<Message> additionalMessages = null;
            int maxDepth = 5;
            int currDepth = 0;
            do {
                long olderThan = additionalMessages == null ?
                        messages.get(messages.size() - 1).id :
                        additionalMessages.get(additionalMessages.size() - 1).id;

                additionalMessages = getMessagesOlderThan(threadId, olderThan, authBearer);

                List<Message> mergedList = new ArrayList<Message>();
                mergedList.addAll(messages);
                mergedList.addAll(additionalMessages);

                messages = mergedList;
                currDepth++;
            } while (additionalMessages != null && !additionalMessages.isEmpty() && currDepth < maxDepth);
        }

        for (Message message : messages) {
            // Add liked by.
            List<User> likedBy = MessageClient.getLikedBy(message.id, authBearer);
            message.likedBy = likedBy;

            // User.
            User user = UserClient.getUser(message.sender_id, authBearer);
            message.user = user;
        }

        CouchDBClient.INSTANCE.saveData(formattedUrl, JSONMapper.INSTANCE.writeValueAsString(messages));

        return messages;
    }

    private static List<Message> getMessagesOlderThan(long threadId, long olderThan, String authBearer) throws Exception {
        String formattedUrl = String.format("https://appsapi.yammer.com/mobile/feeds/v1/thread/%d?inbox_supported_client=true&threaded=false&older_than=%d&include_counts=true&replies_per_thread=10&limit=10", threadId, olderThan);

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

        return Message.fromMessageDtos(JSONMapper.INSTANCE.readValue(stringBuilder.toString(), FeedDto.class).messages);
    }
}
