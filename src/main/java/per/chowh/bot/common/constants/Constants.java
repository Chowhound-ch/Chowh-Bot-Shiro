package per.chowh.bot.common.constants;

import per.chowh.bot.utils.ConfigUtils;

/**
 * @author : Chowhound
 * @since : 2025/8/6 - 10:29
 */
public class Constants {
    public static final String EVENT_FILTER = "eventFilter";


    public static String getVikiUrl() {
        return ConfigUtils.getConfig("VIKI_URL");
    }
}
