package per.chowh.bot.plugins.jm.exception;

/**
 * @author : Chowhound
 * @since : 2025/11/4 - 17:27
 */
public class JmcomicNotFoundException extends RuntimeException{
    public JmcomicNotFoundException(String message) {
        super(message);
    }
}
