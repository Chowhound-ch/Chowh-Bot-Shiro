package per.chowhound.bot.plugins;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.action.common.ActionRaw;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import per.chowhound.bot.config.RabbitmqTopicConfig;
import per.chowhound.bot.utils.JacksonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
public class JmcomicPlugin implements ApplicationRunner {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private BotContainer botContainer;
    @Value("${per.jm.pdf-path}")
    private String pdfPath;
    @Value("${per.jm.base-path}")
    private String basePath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        botContainer.robots.forEach((k, v) -> {
            File file = FileUtil.file(pdfPath + File.separator + 100866 + ".pdf");
            if (file.exists()){
                String fileStr = "file://" + "/opt/jm/pdf/100866.pdf";
                v.uploadGroupFile(811545265, fileStr,  "100866[密码：123].pdf");
                List<String> msgList = new ArrayList<>();
                OneBotMedia img = OneBotMedia.builder().file(fileStr).cache(false);
                msgList.add(MsgUtils.builder().img(img).build());
                List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(825352674L, "ddd", msgList);
                v.sendGroupForwardMsg(811545265, forwardMsg);
            }

        });
    }

    @RabbitListener(queues = {RabbitmqTopicConfig.QUEUE_INFORM_JM_RESP})
    public void receive_sms(Object msg, Message message, Channel channel){
        botContainer.robots.forEach((k, v) -> {
            String string = new String(message.getBody());
            JsonNode node = JacksonUtil.readTree(string);
            JsonNode meta = node.get("meta");
            JsonNode data = node.get("data");
            String jmCode = meta.get("jmCode").asText();
            String type = meta.get("type").asText();
            long number = meta.get("number").asLong();
            long sender = meta.get("sender").asLong();
            File file = FileUtil.file(pdfPath + File.separator + jmCode + ".pdf");
            if (file.exists()){
                String title = data.get("title").asText();
                File view = FileUtil.file(basePath + File.separator + title + File.separator + "00001.jpg");
                List<String> msgList = new ArrayList<>();
                msgList.add(MsgUtils.builder().img(FileUtil.readBytes(view)).text(title).build());
                OneBotMedia img = OneBotMedia.builder().file(file.getAbsolutePath()).cache(false);
                msgList.add(MsgUtils.builder().img(FileUtil.readBytes(file)).build());
                List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(sender, Long.toString(sender), msgList);
                v.uploadGroupFile(number, file.getAbsolutePath(), jmCode + "[密码：123].pdf");
                if ("group".equals(type)) {
                    v.sendGroupForwardMsg(number, forwardMsg);
                } else if ("private".equals(type)) {
                    v.sendPrivateForwardMsg(number, forwardMsg);
                }
            }

        });
        System.out.println("QUEUE_INFORM_SMS msg");
    }

    @AnyMessageHandler
    @MessageHandlerFilter( cmd = "/?[jJ][mM]\\s*(?<jmCode>\\d{6})")
    public void test(Bot bot, AnyMessageEvent event, Matcher matcher) {
        String jmCode = matcher.group("jmCode");
        String type = event.getMessageType();
        Long number = type.equals("group") ? event.getGroupId() : event.getUserId();
        rabbitTemplate.convertAndSend(RabbitmqTopicConfig.EXCHANGE_TOPICS_INFORM,
                "bot.jm.request",
                JacksonUtil.toJsonString(
                        Map.of("jmCode", jmCode, "type", type, "number" , number, "sender", event.getSender().getUserId())));
    }


}
