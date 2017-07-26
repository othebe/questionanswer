package clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupDto {
    @JsonProperty("id")
    public long id;

    @JsonProperty("full_name")
    public String name;
}
