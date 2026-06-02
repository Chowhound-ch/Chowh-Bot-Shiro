package per.chowh.bot.plugins.img.api;

import per.chowh.bot.plugins.img.entity.PixivImg;
import per.chowh.bot.utils.HttpProxyUtils;
import per.chowh.bot.utils.JacksonUtil;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/10/21 - 14:43
 */
public class PixivApi {
    private static final String PIXIV_API = "https://api.lolicon.app/setu/v2";

    public static List<PixivImg> getImage(List<String> tag, boolean r18) throws IOException {
        String res = HttpProxyUtils.doGetStr(getUrl(tag, r18));
        JsonNode node = JacksonUtil.readTree(res);

        if ((node = node.get("data")) != null) {
            return JacksonUtil.readListValue(node, PixivImg.class);
        }
        return null;
    }
    private static String getUrl(List<String> tag, boolean r18) {
        StringBuilder query = new StringBuilder("?r18=" + (r18 ? 1 : 0));

        for (String t : tag) {
            query.append("&tag=").append(URLEncoder.encode(t, StandardCharsets.UTF_8));
        }

        return PIXIV_API + query;
    }

}
