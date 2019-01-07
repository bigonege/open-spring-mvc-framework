package org.springframework.webmvc.servlet;

import org.springframework.context.AnnotationConfigApplicationContext;
import org.springframework.controller.MyController;
import org.springframework.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.RequestMapping;
import org.springframework.stereotype.RequestParam;
import org.springframework.webmvc.HandlerAdapter;
import org.springframework.webmvc.HandlerMapping;
import org.springframework.webmvc.ModelAndView;
import org.springframework.webmvc.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/25 13:43
 * @Description:
 */
public class DispatchSevrlet extends HttpServlet {
    AnnotationConfigApplicationContext context;

    List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    Map<HandlerMapping,HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping,HandlerAdapter>();

    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //this.doPost(req, resp);
        try {
            doDispatch(req,resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("-----------接受请求-----------");
        try {
            doDispatch(req,resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
       // MyController userService = (MyController)context.getBean("myController");
       // userService.getUsers(null,null,"wangwu",333);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        String contextLocation = config.getInitParameter("contextLocation");
        context = new AnnotationConfigApplicationContext(contextLocation);
        //初始化容器
        onRefresh(context);
    }

    private void onRefresh(AnnotationConfigApplicationContext context) {
        initStrategies(context);
    }

    private void initStrategies(AnnotationConfigApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);
        //处理request中URL和Controller中Method的对应关系
        initHandlerMappings(context);
        //适配器，处理请求中的形参的位置
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        //根据返回值model和view，进行赋值
        initViewResolvers(context);
        initFlashMapManager(context);
    }
    private void initHandlerMappings(AnnotationConfigApplicationContext context) {
       //循环context中的所有DedifinitionBean,如果是Controller，则把RequestMapping
        //和方法对应起来，springMvc中，初始化时只对应到Controller，每次请求需要动态寻找方法
        //本次模拟，直接对应到方法
        String[] beanClassNames = context.getDefinitionNames();
        for(String beanName:beanClassNames){
            Object controller = context.getBean(beanName);
            Class<?> clazz = controller.getClass();
            if(!clazz.isAnnotationPresent(Controller.class)) {continue;}
            RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);


            Method[] methods = clazz.getMethods();
            for(Method method:methods){
                String baseUrl = "";
                if(!"".equals(requestMapping.value().trim())){
                    baseUrl = baseUrl + requestMapping.value().trim();
                }
                if(!method.isAnnotationPresent(RequestMapping.class)){continue;}
                RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                if(!"".equals(methodRequestMapping)){
                    baseUrl +=  methodRequestMapping.value().trim();
                }
                if(!"".equals(baseUrl)){
                    baseUrl.replaceAll("/+","/");
                }
                Pattern pattern = Pattern.compile(baseUrl.replaceAll("\\*",".*"));
                this.handlerMappings.add(new HandlerMapping(controller,method,pattern));
                System.out.println("mapping:"+baseUrl+" Method:"+method);
            }
        }
    }

    private void initHandlerAdapters(AnnotationConfigApplicationContext context) {
        if(this.handlerMappings==null){return;}
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            //每一个方法有一个参数列表，那么这里保存的是形参列表
            Map paramMapping = new HashMap<String,Integer>();
            Method method = handlerMapping.getMethod();
            //通过注解RequestParam标注的命名参数
            Annotation[][] pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation annotation : pa[i]) {
                    if(annotation instanceof RequestParam){
                        String paramValue = ((RequestParam) annotation).value();
                        if(!"".equals(paramValue)){
                            paramMapping.put(paramValue,i);
                        }
                    }
                }
            }
            //接下来，我们处理非命名参数
            //只处理Request和Response，其他类型的读取方式比较复杂，可参考springmvc获取参数的方法
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if(parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class){
                     paramMapping.put(parameterType.getName(),i);
                }
            }

            this.handlerAdapters.put(handlerMapping,new HandlerAdapter(paramMapping));
            System.out.println("HandlerAdapter:"+"Method:"+method+",paramMapping:"+paramMapping);
        }
    }

    private void initViewResolvers(AnnotationConfigApplicationContext context) {

        String layouts = context.getConfig().getProperty("templateRoot");
        String file = this.getClass().getClassLoader().getResource(layouts).getFile();
        File files = new File(file);

        for (File listFile : files.listFiles()) {
            this.viewResolvers.add(new ViewResolver(listFile.getName(),listFile));
        }

    }


    private void initRequestToViewNameTranslator(AnnotationConfigApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(AnnotationConfigApplicationContext context) {
    }



    private void initThemeResolver(AnnotationConfigApplicationContext context) {
    }

    private void initLocaleResolver(AnnotationConfigApplicationContext context) {
    }

    private void initMultipartResolver(AnnotationConfigApplicationContext context) {
    }

    private void initFlashMapManager(AnnotationConfigApplicationContext context) {
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        //根据用户请求的URL来获得一个Handler
        HandlerMapping handlerMapping = getHandler(req);
        if(handlerMapping == null){
            resp.getWriter().write("<font size='25' color='red'>404 Not Found</font><br/><font color='green'><i>Copyright@GupaoEDU</i></font>");
            return;
        }
        HandlerAdapter ha = getHandlerAdapter(handlerMapping);

        ModelAndView mv = ha.handle(req,resp,handlerMapping);

        processDispatchResult(resp,mv);

    }

    private void processDispatchResult(HttpServletResponse resp, ModelAndView mv) throws IOException {
        //调用viewResolver的resolveView方法
        if(null == mv){ return;}

        if(this.viewResolvers.isEmpty()){ return;}

        for (ViewResolver viewResolver : viewResolvers) {
            if(!mv.getViewName().equals(viewResolver.getViewName())){ continue; }

            String out = viewResolver.viewResolver(mv);
            if(out != null){
                resp.getWriter().write(out);
                break;
            }

        }

    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handlerMapping) {
        if(this.handlerAdapters.isEmpty()){return  null;}
        return this.handlerAdapters.get(handlerMapping);
    }

    private HandlerMapping getHandler(HttpServletRequest req) {

        if(this.handlerMappings.isEmpty()){ return  null;}

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();

        requestURI.replaceAll(contextPath,"").replaceAll("/+","/");
        requestURI.replace("/dispacthServlet","").replaceAll("/+","/");

        for (HandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(requestURI);
            if(!matcher.matches()){ continue;}
            return handlerMapping;
        }
        return null;
    }

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("/web/getUsers");

        Matcher m = pattern.matcher("/web/getUsers") ;

        System.out.println(m.matches());
    }
}
