package per.chowh.bot.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.permit.service.UserService;
import per.chowh.bot.web.service.AdminAuthService;

@Configuration
@RequiredArgsConstructor
public class AdminWebConfig implements WebMvcConfigurer {
    private final UserService users;
    private final AdminAuthService auth;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                var session = auth.authenticate(request.getHeader("Authorization"));
                var user = users.getByUserId(Long.parseLong(session.userId()));
                if (user.isNull() || user.getRole() == null || user.getRole().getValue() < PermissionEnum.ADMIN.getValue()) {
                    auth.logout(session);
                    response.sendError(403, "需要管理员权限");
                    return false;
                }
                request.setAttribute(AdminAuthService.SESSION_ATTRIBUTE, session);
                return true;
            }
        }).addPathPatterns("/api/admin/**");
    }
}
