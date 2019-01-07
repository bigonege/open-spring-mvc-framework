package Reflect;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/29 11:39
 * @Description:
 */
public class UserService {
    String name ;
    Integer age;

    public String getUser(String name ,Integer age) {
        System.out.println(name+" "+age);
        return name+" "+age;
    }
}
