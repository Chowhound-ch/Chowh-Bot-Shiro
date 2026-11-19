package per.chowh.bot.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.chowh.bot.core.permit.domain.User;
import per.chowh.bot.core.permit.service.UserService;
import per.chowh.bot.web.utils.Result;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.web.service.AdminAuthService;

/**
 * @author : Chowhound
 * @since : 2025/11/13 - 15:23
 */
@RestController
@RequestMapping("/system")
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private AdminAuthService auth;


    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (user.getUserId() == null || user.getPassword() == null || user.getPassword().isBlank()) {
            return Result.fail("请输入 QQ 号和密码");
        }
        User desUser = userService.getByUserId(user.getUserId());
        if (desUser == null || desUser.isNull() || !user.getPassword().equals(desUser.getPassword())) {
            return Result.fail("QQ 号或密码错误");
        }
        if (desUser.getRole() == null || desUser.getRole().getValue() < PermissionEnum.ADMIN.getValue()) {
            return Result.fail("需要管理员权限");
        }
        return Result.success(auth.login(desUser));
    }

}
