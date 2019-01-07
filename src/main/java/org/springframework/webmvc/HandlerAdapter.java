package org.springframework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/30 10:19
 * @Description:
 */
public class HandlerAdapter {
    HandlerMapping handlerMapping;
    private Map<String,Integer> paramMapping;
    public HandlerAdapter(Map<String,Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handlerMapping) throws InvocationTargetException, IllegalAccessException {

        
        //1,获取HanderAdapter

        //2,从req中获取实参，并赋值给Method中的形参
        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();

        Map<String,String[]> parameterMap = req.getParameterMap();
        //3,反射查询出结果model
        Object[] args = new Object[parameterTypes.length];

        //赋值加注解RequestParam的字段
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            if(!paramMapping.containsKey(key)){continue;}

            //String value = Arrays.toString(entry.getValue());
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");

            Integer index = paramMapping.get(key);

            args[index] = caseStringValue(value,parameterTypes[index]);;

        }
        //赋值HttpServletRequest和HttpServletResponse
        if( this.paramMapping.containsKey(HttpServletRequest.class.getName())){
            Integer index = this.paramMapping.get(HttpServletRequest.class.getName());
            args[index] = req;
        }
        if( this.paramMapping.containsKey(HttpServletResponse.class.getName())){
            Integer index = this.paramMapping.get(HttpServletResponse.class.getName());
            args[index] = resp;
        }

        Object model = handlerMapping.getMethod().invoke(handlerMapping.getControler(), args);
        //4,查找到对应的view，然后用model中的值替换view中的占位符
        if(model == null){ return  null; }
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == ModelAndView.class;
        if(isModelAndView){
            return (ModelAndView)model;
        }else{
            return null;
        }

    }

    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class){
            return  Integer.valueOf(value);
        }else if(clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else {
            return null;
        }
    }
}
