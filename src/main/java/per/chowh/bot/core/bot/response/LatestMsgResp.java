package per.chowh.bot.core.bot.response;

import com.mikuac.shiro.dto.action.response.GetMsgListResp;
import lombok.Data;

/**
 * @author : Chowhound
 * @since : 2025/7/9 - 15:33
 */
@Data
public class LatestMsgResp {
    private GetMsgListResp lastestMsg;
    private String peerUin;
    private String remark;
    private String msgTime;
    private Long chatType;
    private String msgId;
    private String sendNickName;
    private String sendMemberName;
    private String peerName;
}
