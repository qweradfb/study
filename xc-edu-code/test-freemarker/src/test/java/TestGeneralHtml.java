import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TestGeneralHtml {
    //基于模板生成静态化文件
    @Test
    public void generalHtmlTest() throws Exception {
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板路径
        String classpath = this.getClass().getResource("/").getPath();
        System.out.println(classpath);
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("test01.ftl");
        //数据模型
        Map<String, Object> map = new HashMap<>();
        map.put("name", "黑马程序员");
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //静态化内容
        System.out.println(content);
        InputStream inputStream = IOUtils.toInputStream(content);
        //输出文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/test1.html"));
        int copy = IOUtils.copy(inputStream, fileOutputStream);
    }
    
    @Test
    public void generalHtmlByStringTest() throws Exception{
        //创建配置类
        Configuration configuration=new Configuration(Configuration.getVersion());
    //模板内容，这里测试时使用简单的字符串作为模板
        String templateString="" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                " 名称：${name}\n" +
                " </body>\n" +
                "</html>";
//模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateString);
        configuration.setTemplateLoader(stringTemplateLoader);
//得到模板
        Template template = configuration.getTemplate("template","utf-8");
//数据模型
        Map<String,Object> map = new HashMap<>();
        map.put("name","黑马程序员");
//静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//静态化内容
        System.out.println(content);
        InputStream inputStream = IOUtils.toInputStream(content);
//输出文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/test1.html"));
        IOUtils.copy(inputStream, fileOutputStream);
    }

}
