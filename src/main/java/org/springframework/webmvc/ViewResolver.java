package org.springframework.webmvc;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: Wang Ky
 * @Date: 2019/1/6 15:32
 * @Description:
 */
public class ViewResolver {
    private String viewName;
    private File templateFile;

    public ViewResolver(String viewName, File templateFile) {
        this.viewName = viewName;
        this.templateFile = templateFile;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }

    public String viewResolver(ModelAndView mv) throws IOException {
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.templateFile, "r");

        String line = null;

        try {
            while (null != (line = ra.readLine())) {
                line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                Matcher m = matcher(line);
                while (m.find()) {
                     for (int i= 1;i<=m.groupCount();i++){
                         //要把￥{}中间的这个字符串给取出来
                         String paramName = m.group(i);
                         Object paramValue = mv.getModel().get(paramName);

                         if (null == paramValue) {
                             continue;
                         }
                         line = line.replaceAll("￥\\{" + paramName + "\\}", paramValue.toString());

                         line = new String(line.getBytes("utf-8"), "ISO-8859-1");

                    }
                }
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ra.close();
        }
        return sb.toString();
    }

    private Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("￥\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }



}
