package Reflect;

import java.lang.reflect.InvocationTargetException;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/29 11:41
 * @Description:
 */
public class Main {
    public static void main(String[] args) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("Reflect.UserService");
            Object instance = clazz.newInstance();

            Object[] objs = new Object[2];
            objs[0]="aaaaaaaaa";
            objs[1]=333;

            clazz.getMethod("getUser", String.class, Integer.class)
                    .invoke(instance,"ffffff",33);
            clazz.getMethod("getUser", String.class, Integer.class)
                    .invoke(instance,objs);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
