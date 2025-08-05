package per.chowh.bot.plugins.jm.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class JmPhoto {
    private String savePath;
    private boolean exists;
    private boolean skip;
    private String photoId;
    private String scrambleId;
    private String name;
    private Integer sort;
    @JsonAlias("_tags")
    private String tags;
    @JsonAlias("_series_id")
    private Integer seriesId;
    @JsonAlias("_author")
    private String author;
    private JmAlbum fromAlbum;
    private Integer index;
    private List<String> pageArr = new ArrayList<>();
    private String dataOriginalDomain;
    private Object dataOriginal0;
    private Object dataOriginalQueryParams;
}
