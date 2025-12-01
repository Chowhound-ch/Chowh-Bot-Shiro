package per.chowh.bot.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import per.chowh.bot.utils.ConfigUtils;
import per.chowh.bot.utils.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author : Chowhound
 * @since : 2025/12/1 - 10:20
 */
@Slf4j
@Configuration
public class DataBaseInitConfig {
    // 执行数据库初始化脚本
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    public static final String SQL_BASE_PATH;
    static {
        SQL_BASE_PATH = ConfigUtils.CONFIG_PATH + "db/";
    }

    @PostConstruct
    public void init() {
        try(SqlSession sqlSession = sqlSessionFactory.openSession()) {
            File file = FileUtil.file(SQL_BASE_PATH);
            File[] files = file.listFiles();
            if (!file.exists() || files == null) {
                log.warn("数据库初始化脚本不存在：{}", SQL_BASE_PATH);
                return;
            }
            for (File sqlFile : files) {
                if (sqlFile.isFile() && "sql".equals(FileUtil.extName(sqlFile))) {
                    log.info("执行数据库初始化脚本：{}", sqlFile.getName());
                    String sql = FileUtil.readString(sqlFile, StandardCharsets.UTF_8);
                    Connection connection = sqlSession.getConnection();
                    try (Statement statement = connection.createStatement()){
                        statement.execute(sql);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
