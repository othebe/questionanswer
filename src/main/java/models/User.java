package models;

import clients.dto.UserDto;
import clients.dto.UserListDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class User {
    public long id;
    public String name;
    public long following;
    public long followers;
    public long updates;
    public List<Group> topGroups;

    public static User fromUserDto(UserDto userDto) {
        User user = new User();
        user.id = userDto.id;
        user.name = userDto.name;
        user.following = userDto.stats.following;
        user.followers = userDto.stats.followers;
        user.updates = userDto.stats.updates;

        if (userDto.topGroups != null) {
            user.topGroups = Group.fromGroupDtos(userDto.topGroups);
        }

        return user;
    }

    public static List<User> fromUserListDto(UserListDto userListDto) {
        List<User> users = new ArrayList<User>();
        for (UserDto userDto : userListDto.users) {
            User user = fromUserDto(userDto);
            users.add(user);
        }

        return users;
    }

    public static User fromMap(Map<String, Object> map) {
        User user = new User();
        user.id = Long.valueOf(map.get("id").toString());
        user.name = map.get("name").toString();
        user.following = Long.valueOf(map.get("following").toString());
        user.followers = Long.valueOf(map.get("followers").toString());
        user.updates = Long.valueOf(map.get("updates").toString());

        List<Group> topGroups = new ArrayList<Group>();
        if (map.containsKey("topGroups")) {
            List<LinkedHashMap<String, Object>> topGroupMaps = (List<LinkedHashMap<String, Object>>) map.get("topGroups");
            if (topGroupMaps != null) {
                for (Map<String, Object> topGroupMap : topGroupMaps) {
                    topGroups.add(Group.fromMap(topGroupMap));
                }
            }
        }
        user.topGroups = topGroups;

        return user;
    }
}
