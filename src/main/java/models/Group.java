package models;

import clients.dto.GroupDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Group {
    public long id;
    public String fullName;

    public static Group fromGroupDto(GroupDto groupDto) {
        Group group = new Group();
        group.id = groupDto.id;
        group.fullName = groupDto.name;

        return group;
    }

    public static List<Group> fromGroupDtos(List<GroupDto> groupDtos) {
        List<Group> groups = new ArrayList<Group>();
        for (GroupDto groupDto : groupDtos) {
            groups.add(fromGroupDto(groupDto));
        }

        return groups;
    }

    public static Group fromMap(Map<String, Object> map) {
        Group group = new Group();
        group.id = Long.valueOf(map.get("id").toString());
        group.fullName = map.get("fullName").toString();

        return group;
    }
}
