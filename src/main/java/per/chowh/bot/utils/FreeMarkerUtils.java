package per.chowh.bot.utils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FreeMarkerUtils {
   
   

    public static final Logger logger = LoggerFactory.getLogger(FreeMarkerUtils.class);

    /**
     * FreemarkerUtils加载index.ftl模板，渲染数据
     * @param template
     * @param map
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public static String getTemplate(String template, Map<String, Object> map) {
        //创建freeMarker配置对象
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        //设置读取模板的基础路径
        cfg.setTemplateLoader(new ClassTemplateLoader(
        FreeMarkerUtils.class.getClass().getClassLoader(), "/templates"));
        //设置编码格式
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.toString());
        //设置模板异常处理
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        //选择日志处理
        cfg.setLogTemplateExceptions(true);
        Template temp = null;
        String result = "";
        try {
            //获取模板问价
            temp = cfg.getTemplate(template, StandardCharsets.UTF_8.toString());
            StringWriter stringWriter = new StringWriter();
            //替换占位符
            temp.process(map, stringWriter);
            stringWriter.flush();
            stringWriter.close();
            //获取ftl内容字符串
            result = stringWriter.getBuffer().toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            logger.info("模板读取异常" + e.getBlamedExpressionString());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成Image
     * @param template 模板地址
     * @param map 替换对象
     * @param width 图片宽度
     * @param height 图片高度
     * @throws Exception
     */
    public static byte[] turnImage(String template, Map<String, Object> map,Integer width,Integer height) {
        //获取freeMarker替换占位符后的字符串
        String html = FreeMarkerUtils.getTemplate(template, map);
        //将字符串转为bytes
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        //跟据document渲染图片
        return renderHtmlToImage(new String(bytes), width, height);
    }

    private static byte[] renderHtmlToImage(String html, int width, int height) {
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.setText(html);
        pane.setSize(width, height);

        // 渲染为图片
        BufferedImage image = new BufferedImage(
                pane.getWidth(),
                pane.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = image.createGraphics();
        pane.paint(g);
        g.dispose();

        // 转为字节数组
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            javax.imageio.ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
