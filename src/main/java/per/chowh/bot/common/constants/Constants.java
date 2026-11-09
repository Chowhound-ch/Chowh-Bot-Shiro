package per.chowh.bot.common.constants;

import per.chowh.bot.utils.ConfigUtils;

/**
 * @author : Chowhound
 * @since : 2025/8/6 - 10:29
 */
public class Constants {
    public static final String EVENT_FILTER = "eventFilter";

    // region 配置文件
    public static final String VIKI_URL = "VIKI_URL";



    // endregion

    public static String getVikiUrl() {
        return ConfigUtils.getConfig(VIKI_URL);
    }
}
