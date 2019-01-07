package org.springframework.context.config;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/25 15:16
 * @Description:
 */
public class BeanDefinition {
    boolean singleton;
    String factoryBeanName;
    String beanClassName;
    String beanName;

    public BeanDefinition() {
    }

    public BeanDefinition(String factoryBeanName, String beanClassName, String beanName) {
        this.singleton = false;
        this.factoryBeanName = factoryBeanName;
        this.beanClassName = beanClassName;
        this.beanName = beanName;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

}
