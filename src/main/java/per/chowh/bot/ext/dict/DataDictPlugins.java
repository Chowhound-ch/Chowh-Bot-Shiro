package per.chowh.bot.ext.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.event.message.PrivateMessageEvent;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgTable;
import per.chowh.bot.ext.dict.domain.DataDictCat;
import per.chowh.bot.ext.dict.domain.DataDictCatDetail;
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

    @EventListener(cmd = "查看字典\\s*(?<page>\\d*)", permit = PermissionEnum.OWNER, name = "查看字典")
    public void showDict(ChowhBot bot, PrivateMessageEvent event, Long page) {
        if (page == null) {
            page = 1L;
        }
        IPage<DataDictCat> iPage = dictService.pageCategories(new Page<>(page, 10));
        List<List<String>> rows = iPage.getRecords().stream()
                .map(category -> List.of(category.getCatCode(), category.getCatName()))
                .toList();
        MsgTable.send(bot, event, "字典分类列表", List.of("主编码", "字典名称"), rows,
                "当前页 " + page + "　总页数 " + iPage.getPages() + "　总记录数 " + iPage.getTotal());
    }

    @EventListener(cmd = "增加字典\\s+(?<catCode>\\S+)\\s+(?<catName>\\S.*)",
            permit = PermissionEnum.OWNER,
            name = "增加字典")
    public void addDict(ChowhBot bot, PrivateMessageEvent event, String catCode, String catName) {
        dictService.saveCategory(catCode, catName);
        MsgTable.send(bot, event, "字典分类已保存", List.of("主编码", "字典名称"),
                List.of(List.of(catCode, catName)), "");
    }

    @EventListener(cmd = "增加字典明细\\s+(?<catCode>\\S+)\\s+(?<dictCode>\\S+)\\s+(?<dictName>\\S.*)",
            permit = PermissionEnum.OWNER, name = "增加字典明细")
    public void addDetail(ChowhBot bot, PrivateMessageEvent event, String catCode, String dictCode, String dictName) {
        if (!dictService.saveDetail(catCode, dictCode, dictName)) {
            bot.sendMsg(event, "字典分类 " + catCode + " 不存在，请先增加字典", true);
            return;
        }
        MsgTable.send(bot, event, "字典明细已保存 · " + catCode, List.of("子编码", "明细名称"),
                List.of(List.of(dictCode, dictName)), "");
    }

    @EventListener(cmd = "查看编码\\s*(?<catCode>\\S+)", permit = PermissionEnum.OWNER, name = "查看字典明细")
    public void showDict(ChowhBot bot, PrivateMessageEvent event, String catCode) {
        List<DataDictCatDetail> details = dictService.listDetails(catCode);
        List<List<String>> rows = details.stream()
                .map(detail -> List.of(detail.getDictCode(), detail.getDictName()))
                .toList();
        MsgTable.send(bot, event, "字典明细 · " + catCode, List.of("子编码", "明细名称"), rows,
                "总记录数 " + details.size());
    }
}
