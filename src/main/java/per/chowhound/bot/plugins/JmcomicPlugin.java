package per.chowhound.bot.plugins;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowhound.bot.config.RabbitmqTopicConfig;
import per.chowhound.bot.utils.JacksonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author : Chowhound
 * @since : 2025/7/29 - 15:28
 */
@Slf4j
@Shiro
@Component
public class JmcomicPlugin {

    @Value("${per.python.path}")
    private String PYTHON_PATH;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {RabbitmqTopicConfig.QUEUE_INFORM_JM_RESP})
    public void receive_sms(Object msg, Message message, Channel channel){
        System.out.println("QUEUE_INFORM_SMS msg"+msg);
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
                        Map.of("jmCode", jmCode, "type", type, "number" , number)));
    }
}
