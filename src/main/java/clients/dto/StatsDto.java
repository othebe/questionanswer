package clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatsDto {
    @JsonProperty("following")
    public long following;

    @JsonProperty("followers")
    public long followers;

    @JsonProperty("updates")
    public long updates;
}
