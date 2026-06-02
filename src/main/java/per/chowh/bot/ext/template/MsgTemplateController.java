package per.chowh.bot.ext.template;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import per.chowh.bot.ext.template.utils.MsgParamsUtils;

import java.util.Map;

/**
 * @author : Chowhound
 * @since : 2025/12/9 - 14:34
 */
@RequestMapping("/template")
@Controller
public class MsgTemplateController {

    @GetMapping
    public ModelAndView index(String uuid, String viewName, ModelAndView model) {
        Map<String, Object> params = MsgParamsUtils.getParams(uuid);
        if (params != null) {
            model.addAllObjects(params);
        }
        model.setViewName(viewName);
        return model;
    }

    @GetMapping("/test")
    public ModelAndView test(ModelAndView model) {
        model.addObject("testId", "测试值");
        model.setViewName("msg/event_flag_list");
        return model;
    }
}
