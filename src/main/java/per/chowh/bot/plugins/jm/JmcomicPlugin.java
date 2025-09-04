package per.chowh.bot.plugins.jm;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.plugins.jm.domain.JmAlbum;
import per.chowh.bot.plugins.jm.domain.JmPhoto;
import per.chowh.bot.plugins.jm.domain.JmRelated;
import per.chowh.bot.utils.ConfigUtils;
import per.chowh.bot.utils.JacksonUtil;
import per.chowh.bot.utils.ProcessUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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

//
//    @Override
//    public void run(ApplicationArguments args) {
//        botContainer.robots.forEach((k, v) -> {
//            File file = FileUtil.file(pdfPath + File.separator + 100866 + ".pdf");
//            if (file.exists()){
//                String fileStr = "file://" + pdfPath + "/100866.pdf";
//                ActionRaw actionRaw = v.uploadPrivateFile(2177621094L, fileStr, "100866[密码：123].pdf");
//                v.uploadGroupFile(811545265, fileStr,  "100866[密码：123].pdf");
//                List<String> msgList = new ArrayList<>();
//                OneBotMedia img = OneBotMedia.builder().file(fileStr).cache(false);
//                msgList.add(MsgUtils.builder().img(img).build());
//                List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(825352674L, "ddd", msgList);
//                v.sendGroupForwardMsg(811545265, forwardMsg);
//            }
//
//        });
//    }

    @EventListener( cmd = "/?[jJ][mM]\\s*(?<jmCode>\\d{5,6})")
    public void jmcomic(Bot bot, AnyMessageEvent event, Matcher matcher) {
        String jmCode = matcher.group("jmCode");
        boolean group = "group".equals(event.getMessageType());
        String res = getRes(jmCode, password);


        JmPhoto node = JacksonUtil.readValue(res, JmPhoto.class);
        if (node == null){
            return;
        }
        String title = node.getName();
        List<String> msgList = new ArrayList<>();
        msgList.add(MsgUtils.builder()
                .img("file://" + imageBlur(jmPath + "img/" + title + "/00001.jpg",
                        jmPath + "img/" + title + "/view.jpg")).build()
        );
        msgList.add("标题：" + "[" + node.getPhotoId() + "]" + title);
        msgList.add("标签：" + String.join(", ",  node.getTags()));
        msgList.add("相关推荐：");
        JmAlbum fromAlbum = node.getFromAlbum();
        if (fromAlbum != null){
            List<JmRelated> relatedList = fromAlbum.getRelatedList();
            if (relatedList != null){
                relatedList.forEach(related -> {
                    MsgUtils builder = MsgUtils.builder();
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

        List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(
                event.getSender().getUserId(),
                event.getSender().getNickname(),
                msgList
        );
        String pdfFile = "file://" + jmPath + "pdf/" + jmCode + ".pdf";
        String pdfName = jmCode + "[pwd：" + password +"].pdf";

        if (group) {
            bot.uploadGroupFile(event.getGroupId(), pdfFile, pdfName);
            bot.sendGroupForwardMsg(event.getGroupId(), forwardMsg);
        }else {
            bot.uploadGroupFile(event.getUserId(), pdfFile, pdfName);
            bot.sendPrivateForwardMsg(event.getUserId(), forwardMsg);
        }
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
