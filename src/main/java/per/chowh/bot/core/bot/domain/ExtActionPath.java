package per.chowh.bot.core.bot.domain;

import com.mikuac.shiro.enums.ActionPath;
import lombok.Getter;

/**
 * @author : Chowhound
 * @since : 2025/7/9 - 15:42
 */
@Getter
public enum ExtActionPath implements ActionPath {

    /**
     * 最近消息列表
     */
    GET_RECENT_CONTACT("get_recent_contact");

    /**
     * 请求路径
     */
    private final String path;
    /**
     * 枚举构造函数
     *
     * @param path 请求路径
     */
    ExtActionPath(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 获取请求路径
     */
    @Override
    public String getPath() {
        return this.path;
    }
}
