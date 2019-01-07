package org.springframework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/30 10:15
 * @Description:
 */
public class HandlerMapping {
    Object controler;
    Method method;
    Pattern pattern;

    public HandlerMapping(Object controler, Method method, Pattern pattern) {
        this.controler = controler;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getControler() {
        return controler;
    }

    public void setControler(Object controler) {
        this.controler = controler;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
