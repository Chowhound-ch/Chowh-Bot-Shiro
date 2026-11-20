package per.chowh.bot.web.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import per.chowh.bot.web.utils.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "per.chowh.bot.web")
public class AdminExceptionHandler {
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Result serviceUnavailable(DataAccessException exception) {
        log.error("后台数据服务暂不可用", exception);
        return Result.fail("数据服务暂不可用，请稍后重试");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result invalidInput(IllegalArgumentException exception) {
        return Result.fail(exception.getMessage());
    }
}
