package org.springframework.beans;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/25 21:20
 * @Description:
 */
public class BeanWrapper {

    Object wrappedInstance;
    //原始的通过反射new出来，要把包装起来，存下来
    Object originalInstance;

    //生成通知事件
    BeanPostProcessor beanPostProcessor;

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }

    public BeanWrapper(Object instance) {
        this.wrappedInstance = instance;
        this.originalInstance = instance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public void setWrappedInstance(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    public void setOriginalInstance(Object originalInstance) {
        this.originalInstance = originalInstance;
    }
}
