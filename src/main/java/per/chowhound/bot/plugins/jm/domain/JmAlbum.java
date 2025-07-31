package per.chowhound.bot.plugins.jm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/7/30 - 11:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JmAlbum {
    private String savePath;
    private Boolean exists;
    private Boolean skip;
    private String albumId;
    private String scrambleId;
    private String name;
    private String description;
    private Integer pageCount;
    private String pubDate;
    private String updateDate;
    private String likes;
    private String views;
    private Integer commentCount;
    private List<Object> works;
    private List<Object> actors;
    private List<String> tags;
    private List<Object> authors;
    /**
     * [
     *   100867,
     *   "1",
     *   "[新桥月白日语社] Title"
     * ]
     */
    @JsonProperty("episode_list")
    private List<List<Object>> episodeList;
    @JsonProperty("related_list")
    private List<JmRelated> relatedList;
}