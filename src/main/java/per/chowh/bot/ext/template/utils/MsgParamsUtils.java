package per.chowh.bot.ext.template.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author : Chowhound
 * @since : 2025/12/9 - 15:49
 */
public class MsgParamsUtils {
    private static final Map<String, Map<String, Object>> MSG_PARAMS = new ConcurrentHashMap<>();
    // 读写锁
    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final Lock rLock = rwl.readLock();
    private static final Lock wLock = rwl.writeLock();
    public static String putParams(Map<String, Object> map) {
        String uuid;
        wLock.lock();
        do {
           uuid = UUID.randomUUID().toString();
        } while (MSG_PARAMS.containsKey(uuid));
        MSG_PARAMS.put(uuid, map);
        wLock.unlock();
        return uuid;
    }
    public static Map<String, Object> getParams(String uuid) {
        rLock.lock();
        Map<String, Object> map = MSG_PARAMS.get(uuid);
        rLock.unlock();
        return map;
    }
}
