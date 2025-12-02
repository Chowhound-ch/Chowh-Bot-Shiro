package per.chowh.bot.ext.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.ext.dict.domain.DataDict;
import per.chowh.bot.ext.dict.service.DataDictService;

import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/12/1 - 15:13
 */
@Component
public class DataDictPlugins {
    @Autowired
    private DataDictService dictService;

    @EventListener(cmd = "查看字典(?<page>\\d*)", permit = PermissionEnum.OWNER, name = "查看字典")
    public void showDict(ChowhBot bot, PrivateMessageEvent event, Long page) {
        if (page == null) {
            page = 1L;
        }
        IPage<String> iPage = dictService.listPage(new Page<>(page, 10));
        List<String> dictCode = iPage.getRecords();
        MsgBuilder builder = new MsgBuilder();
        builder.text("⏺️当前字典列表：").text("\n").text("序号\t字典编码").text("\n");
        for (int i = 0; i < dictCode.size(); i++) {
            builder.text("    " + (i + 1) + "\t " + dictCode.get(i)).text("\n");
            if (i % 10 == 9) {
                builder.text("\n");
            }
        }
        builder.text("⏺️总页数：" + iPage.getPages()).text(",当前页：" + page).text(",总记录数：" + iPage.getTotal());
        bot.sendMsg(event, builder.build(), true);
    }

    @EventListener(cmd = "增加字典(?<dictCode>.+?)\\s+(?<dictName>.+?)\\s+(?<dictValue>.+)",
            permit = PermissionEnum.OWNER,
            name = "增加字典")
    public void addDict(ChowhBot bot, PrivateMessageEvent event, String dictCode, String dictName, String dictValue) {
        DataDict dataDict = new DataDict();
        dataDict.setDictName(dictName);
        dataDict.setDictCode(dictCode);
        dataDict.setDictValue(dictValue);
        dataDict = dictService.saveDataDict(dataDict);
        bot.sendMsg(event, "添加字典成功，字典编码：" +
                dataDict.getDictCode() + ",字典名称：" +
                dataDict.getDictName() + ",字典值：" +
                dataDict.getDictValue(), true);
    }


    @EventListener(cmd = "查看编码(?<dictCode>.+)", permit = PermissionEnum.OWNER, name = "查看字典")
    public void showDict(ChowhBot bot, PrivateMessageEvent event, String dictCode) {
        List<DataDict> dataDictList = dictService.listByDictCode(dictCode);
        MsgBuilder builder = new MsgBuilder();
        builder.text("⏺️字典编码" + dictCode + "明细：").text("\n").text("序号\t字典名称\t字典值").text("\n");
        for (int i = 0; i < dataDictList.size(); i++) {
            builder.text("    " + (i + 1) + "\t " + dataDictList.get(i).getDictName() +
                    "\t" + dataDictList.get(i).getDictValue()).text("\n");
        }
        builder.text("⏺️总记录数：" + dataDictList.size());
        bot.sendMsg(event, builder.build(), true);
    }

}
