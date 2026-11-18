package per.chowh.bot.core.utils;

import com.mikuac.shiro.common.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import per.chowh.bot.ext.dict.service.DataDictService;
import per.chowh.bot.utils.HttpProxyUtils;
import per.chowh.bot.utils.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class BasePlugin {

    @Autowired
    protected DataDictService dataDictService;




}
