package clients;

import clients.dto.UserDto;
import common.CouchDBClient;
import common.JSONMapper;
import models.Group;
import models.Message;
import models.User;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static common.Constants.YAMMER_HOST;

public class UserClient {
    public static User getUser(long userId, String authBearer) throws Exception {
        String formattedUrl = String.format("https://www.yammer.com/api/v1/users/%d.json?include_top_groups=true", userId);

        String cached = CouchDBClient.INSTANCE.readData(formattedUrl, String.class);
        if (cached != null) {
            return JSONMapper.INSTANCE.readValue(cached, User.class);
        }

        URL url = new URL(formattedUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authBearer);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        User user = User.fromUserDto(JSONMapper.INSTANCE.readValue(stringBuilder.toString(), UserDto.class));

        CouchDBClient.INSTANCE.saveData(formattedUrl, JSONMapper.INSTANCE.writeValueAsString(user));

        return user;
    }

    public static List<Group> getUserGroups(long userId, String authBearer) throws Exception {
        String formattedUrl = String.format("%s/api/v1/groups/for_user/%d.json", YAMMER_HOST, userId);

        String cached = CouchDBClient.INSTANCE.readData(formattedUrl, String.class);
        if (cached != null) {
            List<Group> cachedGroups = new ArrayList<Group>();
            List<LinkedHashMap<String, Object>> cachedMaps = JSONMapper.INSTANCE.readValue(cached, List.class);
            for (LinkedHashMap<String, Object> cachedMap : cachedMaps) {
                Group group = new Group();
                group.id = Long.valueOf(cachedMap.get("id").toString());
                group.fullName = (String) cachedMap.get("full_name");

                cachedGroups.add(group);
            }
        }

        URL url = new URL(formattedUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authBearer);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        List<LinkedHashMap<String, Object>> groupsMap = JSONMapper.INSTANCE.readValue(stringBuilder.toString(), List.class);
        List<Group> groups = new ArrayList<Group>();
        for (LinkedHashMap<String, Object> groupData : groupsMap) {
            Group group = new Group();
            group.id = Long.valueOf(groupData.get("id").toString());
            group.fullName = (String) groupData.get("full_name");

            groups.add(group);
        }

        CouchDBClient.INSTANCE.saveData(formattedUrl, JSONMapper.INSTANCE.writeValueAsString(groups));

        return groups;
    }
}
