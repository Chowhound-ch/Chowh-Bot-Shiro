package per.chowh.bot.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.chowh.bot.core.permit.domain.User;
import per.chowh.bot.core.permit.service.UserService;
import per.chowh.bot.web.utils.Result;

/**
 * @author : Chowhound
 * @since : 2025/11/13 - 15:23
 */
@RestController
@RequestMapping("/system")
public class LoginController {
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User desUser = userService.getByUserId(user.getUserId());
        if (desUser == null) {
            return Result.fail();
        }
        if (!user.getPassword().equals(user.getPassword())) {
            return Result.fail();
        }
        return Result.success();
    }

}
