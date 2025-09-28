package per.chowh.bot.plugins.jm;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.plugins.jm.domain.JmAlbum;
import per.chowh.bot.plugins.jm.domain.JmPhoto;
import per.chowh.bot.plugins.jm.domain.JmRelated;
import per.chowh.bot.utils.ConfigUtils;
import per.chowh.bot.utils.JacksonUtil;
import per.chowh.bot.utils.ProcessUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : Chowhound
 * @since : 2025/7/29 - 15:28
 */
@Slf4j
@Component
public class JmcomicPlugin{
    @Value("${per.python.path}")
    private String PYTHON_PATH;
    @Value("${per.jm.img-path}")
    private String jmPath;
    private static final String PYTHON_SCRIPTS_PATH;
    private final String password = "123";

    static {
        PYTHON_SCRIPTS_PATH = ConfigUtils.CONFIG_PATH + "scripts/python/jmcomic/";
    }

    @EventListener(value = "/?[jJ][mM]\\s*(?<jmCode>\\d{3,9})", groupStatus = GroupStatusEnum.FULL)
    public void jmcomic(ChowhBot bot, MessageEvent event, String jmCode) {
        String res = getRes(jmCode, password);
        JmPhoto node = JacksonUtil.readValue(res, JmPhoto.class);
        if (node == null){
            return;
        }

        String pdfFile = "file://" + jmPath + "pdf/" + jmCode + ".pdf";
        String pdfName = jmCode + "[密码" + password +"].pdf";
        bot.uploadFile(event,  pdfFile, pdfName);
        bot.sendForwardMsg(event, getJmMsgList(node));
    }

    @EventListener(value = "(?<jmCode>\\d{5,7})", groupStatus =  GroupStatusEnum.FULL)
    public void jmcomicSimple(ChowhBot bot, MessageEvent event, String jmCode) {
        String res = getRes(jmCode, password);
        JmPhoto node = JacksonUtil.readValue(res, JmPhoto.class);
        if (node == null){
            bot.sendMsg(event, "jm没搜到", true);
            return;
        }
        String pdfFile = "file://" + jmPath + "pdf/" + jmCode + ".pdf";
        String pdfName = jmCode + "[密码" + password +"].pdf";

        bot.uploadFile(event,  pdfFile, pdfName);
        bot.sendForwardMsg(event, getJmMsgList(node));
    }

    private List<String> getJmMsgList(JmPhoto photo) {
        String title = photo.getName();
        List<String> msgList = new ArrayList<>();
        msgList.add(MsgBuilder.builder()
                .img("file://" + imageBlur(jmPath + "img/" + title + "/00001.jpg",
                        jmPath + "img/" + title + "/view.jpg")).build()
        );
        msgList.add("标题：" + "[" + photo.getPhotoId() + "]" + title);
        msgList.add("标签：" + String.join(", ",  photo.getTags()));
        msgList.add("相关推荐：");
        JmAlbum fromAlbum = photo.getFromAlbum();
        if (fromAlbum != null){
            List<JmRelated> relatedList = fromAlbum.getRelatedList();
            if (relatedList != null){
                relatedList.forEach(related -> {
                    MsgBuilder builder = MsgBuilder.builder();
//                    不发图片
//                    if (StrUtil.isNotBlank(related.getImage())){
//                        builder.img(related.getImage());
//                    }
                    builder.text("jm号：" + related.getId() + "\n标题：" + related.getName());
                    if (StrUtil.isNotBlank(related.getAuthor())){
                        builder.text("\n作者：" + related.getAuthor());
                    }
                    msgList.add(builder.build());
                });
            }

        }
        return msgList;
    }


    private String getRes(String jmCode, String password) {
        ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_PATH, PYTHON_SCRIPTS_PATH + "main.py", jmCode);
        Map<String, String> environment = processBuilder.environment();
        environment.put("JM_DIR", jmPath);
        environment.put("JM_PASSWORD", password);
        environment.put("JM_CONFIG", PYTHON_SCRIPTS_PATH + "option.yml");
       return ProcessUtils.exec(processBuilder);
    }
    private String imageBlur(String from, String to){
        ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_PATH, PYTHON_SCRIPTS_PATH + "image_blur.py", from, to);
        return ProcessUtils.exec(processBuilder);
    }

}
