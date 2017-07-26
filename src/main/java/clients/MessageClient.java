package clients;

import clients.dto.UserListDto;
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
import java.util.List;
import java.util.Map;

import static common.Constants.REQUEST_DELAY;

public class MessageClient {
    public static List<User> getLikedBy(long messageId, String authBearer) throws Exception {
        String formattedUrl = String.format("https://www.yammer.com/api/v1/users/liked_message/%d", messageId);

        String cached = CouchDBClient.INSTANCE.readData(formattedUrl, String.class);
        if (cached != null) {
            List<Map<String, Object>> userMaps = JSONMapper.INSTANCE.readValue(cached, List.class);
            List<User> users = new ArrayList<User>();
            for (Map<String, Object> userMap : userMaps) {
                users.add(User.fromMap(userMap));
            }
            return users;
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

        UserListDto userListDto = JSONMapper.INSTANCE.readValue(stringBuilder.toString(), UserListDto.class);
        List<User> users = User.fromUserListDto(userListDto);

        CouchDBClient.INSTANCE.saveData(formattedUrl, JSONMapper.INSTANCE.writeValueAsString(users));

        return users;
    }
}
