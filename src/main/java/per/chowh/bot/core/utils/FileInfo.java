package per.chowh.bot.core.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : Chowhound
 * @since : 2025/9/9 - 15:06
 */
@Data
@AllArgsConstructor
public class FileInfo {
    private String fileName;
    private String url;
    private boolean video;
    private boolean emote;
}
