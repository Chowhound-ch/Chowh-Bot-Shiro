package per.chowh.bot.core.event.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import per.chowh.bot.core.event.entity.Anonymous;

/**
 * <p>AnyMessageEvent class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class AnyMessageEvent extends MessageEvent {

    @JsonProperty("message_id")
    private Integer messageId;

    @JsonProperty("sub_type")
    private String subType;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("real_message_type")
    private String realMessageType;

    @JsonProperty("is_binded_group_id")
    private Boolean isBindedGroupId;

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("anonymous")
    private Anonymous anonymous;

    @JsonProperty("sender")
    private Sender sender;

    @JsonProperty("is_binded_user_id")
    private Boolean isBindedUserId;

    /**
     * sender信息
     */
    @Data
    public static class Sender {

        /**
         * 当 subType 值为 group 时，表示本次私聊事件为临时会话，此时 groupId 为来源 QQ 群
         */
        @JsonProperty("group_id")
        private Long groupId;

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("card")
        private String card;

        @JsonProperty("sex")
        private String sex;

        @JsonProperty("age")
        private Integer age;

        @JsonProperty("area")
        private String area;

        @JsonProperty("level")
        private String level;

        @JsonProperty("role")
        private String role;

        @JsonProperty("title")
        private String title;

    }

}
