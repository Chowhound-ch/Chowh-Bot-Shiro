package per.chowh.bot.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author : Chowhound
 * @since : 2023/07/13 - 13:32
 */
@SuppressWarnings("unused")
public class JsonPath extends ArrayList<String> {

    private JsonPath(Collection<? extends String> c) {
        super(c);
    }

    public static JsonPath of(String... paths) {
       return new JsonPath(Arrays.asList(paths));
   }
}