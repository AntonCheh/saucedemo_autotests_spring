package restApi.pojo.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class User {

    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    private List<UserData> data;
    private Support support;

    @JsonProperty("_meta")
    private Meta meta;
}
