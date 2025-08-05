package per.chowh.bot.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

/**
 * 对于JsonNode的预处理操作
 *
 * <p>可直接使用{@link JsonOperatePretreatment#fromPath(JsonPath)}方法来创建一个取对应Json路径下的Json串的预处理操作
 *
 * @author : Chowhound
 * @since : 2023/07/13 - 12:56
 */
@FunctionalInterface
public interface JsonOperatePretreatment extends Function<JsonNode, JsonNode> {
    static JsonOperatePretreatment fromPath(JsonPath path) {
        return node -> {
            for (String s : path) {
                node = node.get(s);
            }
            return node;
        };
    }

    static JsonOperatePretreatment emptyOperate() {
        return node -> node;
    }
}
