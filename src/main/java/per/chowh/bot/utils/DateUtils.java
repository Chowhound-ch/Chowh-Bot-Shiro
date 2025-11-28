package per.chowh.bot.utils;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author : Chowhound
 * @since : 2025/11/28 - 15:35
 */
public class DateUtils {
    public static final String YYYY_MM_DD_HH_DD_SS_CN = "yyyy年MM月dd HH:mm:ss";
    public static String formatCN(Date date) {
        return DateUtil.format(date, YYYY_MM_DD_HH_DD_SS_CN);
    }
}
