package per.chowh.bot.web.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Chowhound
 * @since : 2025/11/13 - 15:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String code;
    private String msg;
    private Object data;
    public static final String SUCCESS = "0";
    public static final String FAIL = "1000";
    public static final String NOT_FOUND = "1001";

    public static Result success() {
        return new Result(SUCCESS, "操作成功", null);
    }
    public static Result success(Object data) {
        return new Result(SUCCESS, "操作成功", data);
    }
    public static Result fail() {
        return new Result(FAIL, "操作失败", null);
    }
    public static Result fail(String msg) {
        return new Result(FAIL, msg, null);
    }
    public static Result fail(String code, String msg) {
        return new Result(code, msg, null);
    }
}
