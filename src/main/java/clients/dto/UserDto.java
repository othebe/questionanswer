package clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.Group;

import java.util.List;

public class UserDto {
    @JsonProperty("id")
    public long id;

    @JsonProperty("full_name")
    public String name;

    @JsonProperty("stats")
    public StatsDto stats;

    @JsonProperty("top_groups")
    public List<GroupDto> topGroups;

    @JsonProperty("")
    public List<Group> groups;
}
