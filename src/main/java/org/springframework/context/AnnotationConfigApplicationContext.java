package org.springframework.context;

import org.springframework.beans.BeanWrapper;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.config.BeanDefinition;
import org.springframework.stereotype.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/25 13:46
 * @Description:
 */
public class AnnotationConfigApplicationContext {
    //保存一个读取注解的Bean定义读取器，并将其设置到容器中
    private final AnnotatedBeanDefinitionReader reader;
    //保存一个扫描指定类路径中注解Bean定义的扫描器，并将其设置到容器中
    private final ClassPathBeanDefinitionScanner scanner;

    Set<BeanDefinition> beanDefinitions;



    /** Cache of unfinished FactoryBean instances: FactoryBean name --> BeanWrapper */
    private final Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>(16);

    private final String contextLocation;
    //默认构造函数，初始化一个空容器，容器不包含任何 Bean 信息，需要在稍后通过调用其register()
    //方法注册配置类，并调用refresh()方法刷新容器，触发容器对注解Bean的载入、解析和注册过程
    public AnnotationConfigApplicationContext(String contextLocation) {
        this.contextLocation = contextLocation;
        this.reader = new AnnotatedBeanDefinitionReader(contextLocation);
        this.scanner = new ClassPathBeanDefinitionScanner();
        refresh();
    }
    public void refresh(){
        //1，定位，从配置文件中读取要扫描的class
        String[] basePackages = reader.doLocation(contextLocation);
        //2，加载
        List<String> beanClassesName = scanner.doScan(basePackages);
        //3，注册
        this.reader.doRegisterBean(beanClassesName);
        //4，自动注入依赖的bean
        doAutowired(this.reader.getBeanDefinitions());

    }

    private void doAutowired(Map<String,BeanDefinition> beanDefinitions) {

        if(null != beanDefinitions){
            for (Map.Entry<String,BeanDefinition> beanDefinition: beanDefinitions.entrySet()){
                //调用getBean方法进行实例化
                String beanName = beanDefinition.getKey();
                if(!beanDefinition.getValue().isSingleton()){
                    Object install = this.reader.getBean(beanName);
                }
            }
        }
        Map<String, BeanWrapper> beanWrapperMap = this.reader.getBeanWrapperMap();
        for(Map.Entry<String,BeanWrapper> beanWrapperEntry : beanWrapperMap.entrySet()){
            populateBean(beanWrapperEntry.getKey(),beanWrapperEntry.getValue().getOriginalInstance());
        }
    }

    private void populateBean(String key, Object originalInstance) {
        Class clazz = originalInstance.getClass();
        //只对Controller、service中的filed进行赋值
        if(!(clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Service.class))){
            return;
        }

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields){
            if(!field.isAnnotationPresent(Autowired.class)) {continue;}

            Autowired annotation = field.getAnnotation(Autowired.class);
            String annotationName = annotation.value().trim();

            if("".equals(annotationName)){//如果Autowired上没有赋值，则去类名首字母小写值

                annotationName = field.getType().getName();

            }
            field.setAccessible(true);

            try {
                field.set(originalInstance,this.reader.getBeanWrapperMap().get(annotationName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }


    }

    public Object getBean(String beanName){
        return this.reader.getBeanWrapperMap().get(beanName).getWrappedInstance();
    }

    public String[] getDefinitionNames(){
        return this.reader.getDefinitionBeanNames();
    }

    public Properties getConfig() {
        return this.reader.getProperties();
    }
}
