package clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FeedDto {
    @JsonProperty("messages")
    public List<MessageDto> messages;
}
