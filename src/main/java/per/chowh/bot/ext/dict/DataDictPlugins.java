package per.chowh.bot.ext.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.event.message.PrivateMessageEvent;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.ext.dict.domain.DataDict;
import per.chowh.bot.ext.dict.service.DataDictService;
import per.chowh.bot.utils.FreeMarkerUtils;
import per.chowh.bot.utils.PlaywrightScreenshot;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        List<List<String>> rows = iPage.getRecords().stream().distinct()
                .map(code -> List.of(Objects.toString(code, "—")))
                .toList();
        sendTable(bot, event, "字典编码列表", List.of("字典编码"), rows,
                "当前页 " + page + "　总页数 " + iPage.getPages() + "　总记录数 " + iPage.getTotal());
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
        sendTable(bot, event, "添加字典成功", List.of("字典编码", "字典名称", "字典值"),
                List.of(List.of(dataDict.getDictCode(), dataDict.getDictName(), dataDict.getDictValue())), "");
    }


    @EventListener(cmd = "查看编码(?<dictCode>.+)", permit = PermissionEnum.OWNER, name = "查看字典")
    public void showDict(ChowhBot bot, PrivateMessageEvent event, String dictCode) {
        List<DataDict> dataDictList = dictService.listByDictCode(dictCode);
        List<List<String>> rows = dataDictList.stream()
                .map(dict -> List.of(Objects.toString(dict.getDictName(), "—"),
                        Objects.toString(dict.getDictValue(), "—")))
                .toList();
        sendTable(bot, event, "字典明细 · " + dictCode, List.of("字典名称", "字典值"), rows,
                "总记录数 " + dataDictList.size());
    }

    private void sendTable(ChowhBot bot, PrivateMessageEvent event, String title,
                           List<String> columns, List<List<String>> rows, String summary) {
        String html = FreeMarkerUtils.getTemplate("msg/data_dict.ftl", Map.of(
                "title", title, "columns", columns, "rows", rows, "summary", summary));
        if (html.isBlank()) {
            throw new IllegalStateException("字典表格模板渲染失败");
        }
        byte[] image = PlaywrightScreenshot.takeHtmlScreenshot(html, ".dict-card");
        bot.sendMsg(event, new MsgBuilder().img(image).build(), false);
    }
}
