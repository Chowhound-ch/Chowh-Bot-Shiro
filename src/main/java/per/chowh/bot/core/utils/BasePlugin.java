package per.chowh.bot.core.utils;

import com.mikuac.shiro.common.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import per.chowh.bot.ext.dict.domain.DataDictCatDetail;
import per.chowh.bot.ext.dict.service.DataDictService;
import per.chowh.bot.utils.HttpProxyUtils;
import per.chowh.bot.utils.HttpUtils;
import per.chowh.bot.utils.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class BasePlugin {
    private static final Pattern CQ_IMAGE =
            Pattern.compile("\\[CQ:image,(?:[^,\\[\\]]+,)*?file=([^,\\[\\]]+)(?:,[^\\[\\]]*)?\\]");
    @Autowired
    protected DataDictService dataDictService;



    public String parseTextImageMsg(String msg) {
        msg = StringUtils.parseByEnvironment(msg);
        MsgBuilder builder = MsgBuilder.builder();
        Matcher matcher = CQ_IMAGE.matcher(msg);
        List<DataDictCatDetail> proxyDomains = null;
        int textStart = 0;
        while (matcher.find()) {
            builder.text(msg.substring(textStart, matcher.start()));
            textStart = matcher.end();

            HttpUrl url = HttpUrl.parse(ShiroUtils.unescape(matcher.group(1)));
            if (url == null) {
                builder.text(matcher.group());
                continue;
            }
            if (proxyDomains == null) {
                proxyDomains = dataDictService.listDetails("PROXY_DOMAINS");
            }
            boolean useProxy = proxyDomains.stream()
                    .anyMatch(entry -> url.host().equalsIgnoreCase(entry.getDictName()));
            appendNetworkImage(builder, url, useProxy);
        }
        return builder.text(msg.substring(textStart)).build();
    }

    private void appendNetworkImage(MsgBuilder builder, HttpUrl url, boolean useProxy) {
        try (Response response = useProxy ? HttpProxyUtils.doGet(url.toString())
                : HttpUtils.doGet(null, url.toString(), null)) {
            if (response.isSuccessful()) {
                byte[] bytes = response.body().bytes();
                if (bytes.length > 0) {
                    builder.img(bytes);
                    return;
                }
            }
            log.warn("图片下载失败，主机 {}，HTTP 状态 {}，url {}", url.host(), response.code(), url);
        } catch (IOException e) {
            log.warn("图片下载失败，主机 {}，错误类型 {}，url {}", url.host(), e.getClass().getSimpleName(), url);
        }
        builder.text("[图片下载失败]");
    }

}
