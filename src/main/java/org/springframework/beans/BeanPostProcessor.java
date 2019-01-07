package org.springframework.beans;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/27 16:33
 * @Description:
 */
public class BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }
    public Object postProcessAfterInitialization(Object bean, String beanName){
        return bean;
    }
}
