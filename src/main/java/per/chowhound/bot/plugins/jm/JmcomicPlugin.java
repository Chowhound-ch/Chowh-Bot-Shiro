package per.chowhound.bot.plugins.jm;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowhound.bot.plugins.jm.domain.JmDownMsg;
import per.chowhound.bot.utils.JacksonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author : Chowhound
 * @since : 2025/7/29 - 15:28
 */
@Slf4j
@Shiro
@Component
public class JmcomicPlugin{
    @Value("${per.python.path}")
    private String PYTHON_PATH;
    @Value("${per.jm.img-path}")
    private String jmPath;
    private final String password = "123";

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

    @AnyMessageHandler
    @MessageHandlerFilter( cmd = "/?[jJ][mM]\\s*(?<jmCode>\\d{5,6})")
    public void jmcomic(Bot bot, AnyMessageEvent event, Matcher matcher) {
        String jmCode = matcher.group("jmCode");
        boolean group = "group".equals(event.getMessageType());
        String res = getRes(jmCode, password);

        JmDownMsg node = JacksonUtil.readValue(res, JmDownMsg.class);
        if (node == null){
            return;
        }
        String title = node.getTitle();
        List<String> msgList = new ArrayList<>();
        msgList.add(MsgUtils.builder()
                .img("file://" + jmPath + "img/" + title + "/view.jpg").build()
        );
        msgList.add("标题：" + title);
        msgList.add("标签：" + String.join(", ",  node.getTags()));
        msgList.add("相关推荐：");
        node.getRelatedList().forEach(related -> {
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
        URL mainUrl = ClassLoaderUtil.getClassLoader().getResource("scripts/python/jmcomic/main.py");
        ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_PATH, mainUrl.getFile(), jmCode);
        Map<String, String> environment = processBuilder.environment();
        environment.put("JM_DIR", jmPath);
        environment.put("JM_PASSWORD", password);
        StringBuilder sb = new StringBuilder();
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine())!= null) {
                sb.append(line);
            }
            int exitCode = process.waitFor();
            log.error("Python script exited with code: {}", exitCode);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return sb.toString();
    }

}
