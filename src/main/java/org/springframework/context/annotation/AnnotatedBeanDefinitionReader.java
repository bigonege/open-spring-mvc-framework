package org.springframework.context.annotation;

import org.springframework.beans.BeanPostProcessor;
import org.springframework.beans.BeanWrapper;
import org.springframework.context.config.BeanDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/25 13:47
 * @Description:
 */
public class AnnotatedBeanDefinitionReader {

    private final String contextLocation;

    Properties properties = new Properties();

    String [] basePackages;

    Set<BeanDefinition> beanDefinitionsSet ;

    Map<String,Object> beanCacheMap = new HashMap<String, Object>();

    //用来存储所有的被代理过的对象
    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();

    /** Map of bean definition objects, keyed by bean name */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);

    private final static String SCAN_PACKAGE = "scanPackage";

    public AnnotatedBeanDefinitionReader(String contextLocation) {
        this.contextLocation = contextLocation;
        //doLocation(contextLocation);
    }

    public String[]  doLocation(String contextLocation){
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextLocation.replaceAll("classpath:",""));
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null!=resourceAsStream){
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  getBasePackages(properties);
    }

    private String[] getBasePackages(Properties properties) {
        String scanPackage = properties.getProperty(SCAN_PACKAGE);
        if(scanPackage != null){
            this.basePackages = scanPackage.split(",");
        }
        return this.basePackages;
    }

    public Properties getProperties(){
        return this.properties;
    }
    public Set<BeanDefinition> doRegisterBean(List<String> beanClassesName) {

        if(beanClassesName != null){
            //循环className，生成对应的BeanDefinition
            //beanName有三种情况:
            //1、默认是类名首字母小写
            //2、自定义名字
            //3、接口注入
            for (String className:beanClassesName){

                try {
                    Class<?> clazz = Class.forName(className);
                    if(clazz.isInterface()){continue;}
                    BeanDefinition beanDefinition = registerBean(className);

                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces){
                        //如果是多个实现类，一般会报错
                       this.beanDefinitionMap.put(i.getName(),beanDefinition);
                    }

                }catch (Exception e){

                }

            }
        }

        return beanDefinitionsSet;

    }

    private BeanDefinition registerBean(String className) {
        BeanDefinition beanDefinition  = new BeanDefinition();
        String beanName = lowerFristCase(className.substring(className.lastIndexOf(".")+1));
        beanDefinition.setBeanClassName(className);
        beanDefinition.setFactoryBeanName(beanName);
        //beanDefinitionsSet.add(beanDefinition);
        beanDefinitionMap.put(beanName,beanDefinition);
        return beanDefinition;
    }

    private String lowerFristCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Map<String, BeanDefinition> getBeanDefinitions(){
        return this.beanDefinitionMap;
    }

    public Object getBean(String  beanName) {
        if(!this.beanDefinitionMap.containsKey(beanName)){return null;}

        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = instantionBean(beanDefinition);
        if(null == instance){ return  null;}

        //生成通知事件
        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
        //在实例初始化以前调用一次
        beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        beanWrapper.setBeanPostProcessor(beanPostProcessor);
        //在实例初始化以后调用一次
        beanPostProcessor.postProcessAfterInitialization(instance,beanName);
        this.beanWrapperMap.put(beanName,beanWrapper);
        //通过这样一调用，相当于给我们自己留有了可操作的空间
        return this.beanWrapperMap.get(beanName).getWrappedInstance();
    }
    //传一个BeanDefinition，就返回一个实例Bean
    private Object instantionBean(BeanDefinition beanDefinition) {
        String className =  beanDefinition.getBeanClassName();
        Object instance = null;
        if(null != className){
            try {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                if(this.beanCacheMap.containsKey(className)){
                    return this.beanCacheMap.get(className);
                }
                this.beanCacheMap.put(className,instance);
                return instance;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }
        return instance;
    }

    public Map<String,BeanWrapper> getBeanWrapperMap(){
        return this.beanWrapperMap;
    }
    public String[] getDefinitionBeanNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }
}
