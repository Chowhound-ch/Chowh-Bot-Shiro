package per.chowhound.bot.plugins.jm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/7/30 - 11:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JmDownMsg {
    private String name;
    private List<JmRelated> relatedList;
    private String title;
    private List<String> tags;
}
