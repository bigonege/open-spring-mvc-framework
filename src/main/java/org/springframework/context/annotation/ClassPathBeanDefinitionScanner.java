package org.springframework.context.annotation;

import org.springframework.context.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/25 13:54
 * @Description:
 */
public class ClassPathBeanDefinitionScanner {
    private final String[] baseLocations;
    Set<BeanDefinition> beanDefinitions ;
    List<String> beanClassesName =  new ArrayList<String>();
    public ClassPathBeanDefinitionScanner(String...baseLocations) {
        this.baseLocations = baseLocations;
    }

    //TODO
    //更加basePackages，把class扫描出来存到BeanDefinition中
    public List<String> doScan(String... basePackages) {
        //传统扫描注解bean的方式
       if (basePackages != null){
           for (String basePackage:basePackages) {

               URL url = this.getClass().getClassLoader().getResource("/"+basePackage.replaceAll("\\.","/"));
               if(null == url ) continue;
               File classDir = new File(url.getFile());
               if(null == classDir ) continue;
               for (File file : classDir.listFiles()){
                   if (file.isDirectory()){
                       //递归调用
                       doScan(basePackage+"."+file.getName());
                   }else{
                       //把class保存起来
                       this.beanClassesName.add(basePackage+"."+file.getName().replace(".class",""));
                   }
               }

           }
        }
       return beanClassesName;
    }

    public Set<BeanDefinition> getBeanDefinitions(){
      return this.beanDefinitions;
    }
}
