package clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserListDto {
    @JsonProperty("users")
    public List<UserDto> users;
}
