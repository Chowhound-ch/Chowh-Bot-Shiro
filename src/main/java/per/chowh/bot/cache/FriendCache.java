//package per.chowh.bot.cache;
//
//import com.mikuac.shiro.core.Bot;
//import com.mikuac.shiro.dto.action.common.ActionList;
//import com.mikuac.shiro.dto.action.response.FriendInfoResp;
//import jakarta.annotation.PostConstruct;
//import org.ehcache.Cache;
//import org.ehcache.CacheManager;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.ExpiryPolicyBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.config.units.MemoryUnit;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//import per.chowh.bot.utils.BotUtils;
//
//import java.time.Duration;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @author : Chowhound
// * @since : 2025/8/6 - 13:58
// */
//@Deprecated
//@Component
//public class FriendCache implements ApplicationRunner {
//    private static CacheManager cacheManager;
//    private static Cache<Long, FriendInfoResp> friendCache;
//
//
//    public FriendCache(CacheManager cacheManager) {
//        FriendCache.cacheManager = cacheManager;
//    }
//
//    @PostConstruct
//    public void init(){
//        // 创建缓存
//        friendCache = cacheManager.createCache("friend",
//                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, FriendInfoResp.class,
//                                ResourcePoolsBuilder.newResourcePoolsBuilder()
//                                        .heap(20, MemoryUnit.MB)
//                                        .build())
//                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(60)))
//                        .build());
//    }
//
//
//    public static FriendInfoResp get(Long userId) {
//        if (friendCache == null) return getRemote(userId);
//        FriendInfoResp resp = friendCache.get(userId);
//        if (resp != null) { //有缓存直接返回
//            return resp;
//        }
//        FriendInfoResp remote = getRemote(userId);
//        if (remote != null) {
//            friendCache.put(userId, remote);
//        }
//        return remote;
//    }
//
//    // 从远程获取
//    private static FriendInfoResp getRemote(Long userId) {
//        Bot bot = BotUtils.getBot();
//        if (bot == null) return null;
//        ActionList<FriendInfoResp> friendList = bot.getFriendList();
//        for (FriendInfoResp resp : friendList.getData()) {
//            if (Objects.equals(resp.getUserId(), userId)) {
//                return resp;
//            }
//        }
//        return null;
//    }
//
//    // 一次刷新所有缓存
//    public static void refresh() {
//        Bot bot = BotUtils.getBot();
//        if (bot == null) return;
//        ActionList<FriendInfoResp> friendList = bot.getFriendList();
//        List<FriendInfoResp> friendListData = friendList.getData();
//        friendListData.forEach(resp -> friendCache.put(resp.getUserId(), resp));
//    }
//
//    @Override
//    public void run(ApplicationArguments args) {
//        refresh();
//    }
//}
