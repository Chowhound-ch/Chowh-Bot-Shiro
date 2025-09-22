package per.chowh.bot.ext.msg.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/9/18 - 13:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String type;
    private List<MessageNode> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class MessageContent{
        private Sender sender;
        private List<Message> message;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class MessageNode {
        private String id;
        private MessageContent content;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Sender {
        private String userId;
        private String nickname;
        private String card;
    }
}
