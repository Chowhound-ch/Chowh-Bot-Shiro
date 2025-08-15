package per.chowh.bot.core.bot.response;

import com.mikuac.shiro.dto.action.response.GetMsgResp;
import lombok.Data;

/**
 * @author : Chowhound
 * @since : 2025/7/9 - 15:33
 */
// 蛇型转小驼峰

@Data
public class LatestMsgResp {
    private GetMsgResp lastestMsg;
    private String peerUin;
    private String remark;
    private String msgTime;
    private Long chatType;
    private String msgId;
    private String sendNickName;
    private String sendMemberName;
    private String peerName;
}
