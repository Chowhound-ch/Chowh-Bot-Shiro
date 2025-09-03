package per.chowh.bot.core.exception;

/**
 * @author : Chowhound
 * @since : 2025/9/3 - 14:50
 */
public class EventFiledException extends Exception {
    public EventFiledException(String message) {
        super(message);
    }

    public EventFiledException(Throwable cause) {
        super(cause);
    }
}
