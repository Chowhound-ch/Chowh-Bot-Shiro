package per.chowh.bot.core.bot.action;

import com.mikuac.shiro.action.NapCatExtend;
import com.mikuac.shiro.dto.action.common.ActionData;
import per.chowh.bot.core.bot.response.LatestMsgResp;

import java.util.List;

/**
 * 拓展NapcatApi,{@code NapCatExtend}不全
 *
 * @author : Chowhound
 * @since : 2025/7/9 - 14:41
 * @see NapCatExtend
 */
public interface NapcatExtendApi {

    ActionData<List<LatestMsgResp>> getRecentContact(int count);

}
