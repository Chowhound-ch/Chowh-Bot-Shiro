package per.chowh.bot.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import per.chowh.bot.core.bot.domain.ChowhBotContainer;
import per.chowh.bot.ext.dict.domain.DataDictCat;
import per.chowh.bot.ext.dict.domain.DataDictCatDetail;
import per.chowh.bot.ext.dict.mapper.DataDictCatMapper;
import per.chowh.bot.ext.dict.service.DataDictService;
import per.chowh.bot.ext.distribute.domain.EventPermit;
import per.chowh.bot.ext.distribute.service.EventPermitService;
import per.chowh.bot.plugins.kfc.domain.KfcData;
import per.chowh.bot.plugins.kfc.service.KfcDataService;
import per.chowh.bot.web.domain.BotLog;
import per.chowh.bot.web.mapper.AdminLogMapper;
import per.chowh.bot.web.utils.Result;
import per.chowh.bot.web.domain.AdminSession;
import per.chowh.bot.web.service.AdminAuthService;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final DataDictService dictionaries;
    private final DataDictCatMapper categories;
    private final KfcDataService kfc;
    private final EventPermitService subscriptions;
    private final AdminLogMapper logs;
    private final ChowhBotContainer bots;
    private final AdminAuthService auth;

    @GetMapping("/session")
    public Result session(HttpServletRequest request) {
        AdminSession session = (AdminSession) request.getAttribute(AdminAuthService.SESSION_ATTRIBUTE);
        return Result.success(Map.of("userId", session.userId(), "expiresAt", session.expiresAt()));
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        auth.logout((AdminSession) request.getAttribute(AdminAuthService.SESSION_ATTRIBUTE));
        return Result.success();
    }

    @GetMapping("/overview")
    public Result overview() {
        List<String> online = new ArrayList<>();
        bots.forEach((id, bot) -> online.add(id.toString()));
        return Result.success(Map.of("onlineBots", online, "categories", categories.selectCount(null),
                "kfc", kfc.count(), "subscriptions", subscriptions.count(), "logs", logs.selectCount(null)));
    }

    @GetMapping("/categories")
    public Result categories() {
        return Result.success(categories.selectList(new LambdaQueryWrapper<DataDictCat>().orderByAsc(DataDictCat::getCatCode)));
    }

    @PostMapping("/categories")
    public Result saveCategory(@RequestBody DataDictCat category) {
        dictionaries.saveCategory(required(category.getCatCode()), required(category.getCatName()));
        return Result.success();
    }

    @GetMapping("/details")
    public Result details(@RequestParam String catCode) {
        return Result.success(dictionaries.listDetails(required(catCode)));
    }

    @PostMapping("/details")
    public Result saveDetail(@RequestBody DataDictCatDetail detail) {
        String category = required(detail.getCatCode());
        if (categories.selectById(category) == null) throw new IllegalArgumentException("字典分类不存在");
        return dictionaries.saveDetail(category, required(detail.getDictCode()), required(detail.getDictName()))
                ? Result.success() : Result.fail("保存失败");
    }

    @GetMapping("/kfc")
    public Result kfc(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String keyword) {
        return Result.success(kfc.page(new Page<>(Math.max(1, page), 15),
                new LambdaQueryWrapper<KfcData>().like(!keyword.isBlank(), KfcData::getKfcText, keyword)
                        .orderByDesc(KfcData::getDataId)));
    }

    public record KfcRequest(Integer dataId, String kfcText) {}

    @PostMapping("/kfc")
    public Result saveKfc(@RequestBody KfcRequest input) {
        if (input.kfcText() == null || input.kfcText().isBlank()) throw new IllegalArgumentException("请填写文案");
        KfcData data = input.dataId() == null ? new KfcData() : kfc.getById(input.dataId());
        if (data == null) throw new IllegalArgumentException("文案不存在");
        data.setKfcText(input.kfcText());
        data.setModifyTime(new Date());
        if (data.getDataId() == null) data.setCreateTime(data.getModifyTime());
        return kfc.saveOrUpdate(data) ? Result.success() : Result.fail("保存失败");
    }

    @DeleteMapping("/kfc/{id}")
    public Result deleteKfc(@PathVariable Integer id) {
        return kfc.removeById(id) ? Result.success() : Result.fail("文案不存在");
    }

    @GetMapping("/subscriptions")
    public Result subscriptions(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String keyword) {
        return Result.success(subscriptions.page(new Page<>(Math.max(1, page), 15),
                new LambdaQueryWrapper<EventPermit>().like(!keyword.isBlank(), EventPermit::getEventFlag, keyword)
                        .orderByDesc(EventPermit::getPermitId)));
    }

    public record SubscriptionRequest(String eventFlag, Integer permitType, String target) {}

    @PostMapping("/subscriptions")
    public Result saveSubscription(@RequestBody SubscriptionRequest input) {
        String flag = required(input.eventFlag());
        if (dictionaries.listDetails("EVENT_FLAG").stream().noneMatch(item -> flag.equals(item.getDictCode()))) {
            throw new IllegalArgumentException("订阅编码不存在");
        }
        if (!Objects.equals(input.permitType(), 0) && !Objects.equals(input.permitType(), 1)) {
            throw new IllegalArgumentException("请选择订阅目标类型");
        }
        long target;
        try {
            target = Long.parseLong(required(input.target()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("请输入有效 QQ 号或群号");
        }
        if (target <= 0) throw new IllegalArgumentException("请输入有效 QQ 号或群号");
        EventPermit permit = new EventPermit();
        permit.setEventFlag(flag);
        permit.setPermitType(input.permitType());
        permit.setTarget(target);
        subscriptions.saveEventPermitList(List.of(permit));
        return Result.success();
    }

    @DeleteMapping("/subscriptions/{id}")
    public Result deleteSubscription(@PathVariable Integer id) {
        return subscriptions.removeById(id) ? Result.success() : Result.fail("订阅不存在");
    }

    @GetMapping("/logs")
    public Result logs(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String keyword) {
        return Result.success(logs.selectPage(new Page<>(Math.max(1, page), 15),
                new LambdaQueryWrapper<BotLog>().and(!keyword.isBlank(), query -> query
                        .like(BotLog::getMethodId, keyword).or().like(BotLog::getUserId, keyword)
                        .or().like(BotLog::getGroupId, keyword).or().like(BotLog::getCommand, keyword))
                        .orderByDesc(BotLog::getLogId)));
    }

    private String required(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("请填写必填字段");
        return value.trim();
    }
}
