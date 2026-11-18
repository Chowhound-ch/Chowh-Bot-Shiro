package per.chowh.bot.core.registery.mapper;

import org.apache.ibatis.annotations.Param;
import per.chowh.bot.core.registery.domain.EventInvokeLog;

public interface BotLogMapper {
    void insert(@Param("methodId") String methodId, @Param("entry") EventInvokeLog entry);
}
