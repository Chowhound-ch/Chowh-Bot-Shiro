package per.chowh.bot.plugins.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : Chowhound
 * @since : 2025/11/28 - 14:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EpicFreeGameInfo {
    private String id;
    private String title;
    private String cover;
    private Integer originalPrice;
    private String originalPriceDesc;
    private String description;
    private String seller;
    private Boolean isFreeNow;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date freeStart;
    private Long freeStartAt;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date freeEnd;
    private Long freeEndAt;
    private String link;
}
