package per.chowh.bot.utils;

/**
 * @author : Chowhound
 * @since : 2025/9/4 - 11:12
 */
public class StringUtils {

    @SuppressWarnings("unchecked")
    public static <T> T convertStringToType(String str, Class<T> targetType) {
        if (str == null) {
            return null;
        }
        if (targetType == Integer.class || targetType == int.class) {
            return (T) Integer.valueOf(str);
        } else if (targetType == Double.class || targetType == double.class) {
            return (T) Double.valueOf(str);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return (T) Boolean.valueOf(str);
        } else if (targetType == Long.class || targetType == long.class) {
            return (T) Long.valueOf(str);
        } else if (targetType == Float.class || targetType == float.class) {
            return (T) Float.valueOf(str);
        } else if (targetType == Short.class || targetType == short.class) {
            return (T) Short.valueOf(str);
        } else if (targetType == Byte.class || targetType == byte.class) {
            return (T) Byte.valueOf(str);
        } else if (targetType == Character.class || targetType == char.class) {
            if (str.length() == 1) {
                return (T) Character.valueOf(str.charAt(0));
            } else {
                throw new IllegalArgumentException("String must be exactly 1 character long for char conversion");
            }
        } else if (targetType == String.class) {
            return (T) str;
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + targetType);
        }
    }
}
