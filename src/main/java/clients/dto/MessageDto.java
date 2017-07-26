package clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.User;

public class MessageDto {
    @JsonProperty("id")
    public long id;

    @JsonProperty("body")
    public BodyDto body;

    @JsonProperty("likes_count")
    public int likesCount;

    @JsonProperty("sender_id")
    public long sender_id;

    @JsonProperty("group_id")
    public long groupId;

    public User user;
}
