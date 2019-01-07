package org.springframework.controller;

import org.springframework.service.UserService;
import org.springframework.stereotype.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.RequestMapping;
import org.springframework.stereotype.RequestParam;
import org.springframework.webmvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/25 14:10
 * @Description:
 */
@Controller
@RequestMapping("/web")
public class MyController {
    @Autowired
    UserService userService;

    @RequestMapping("/getUsers")
    public void getUsers(HttpServletRequest request, HttpServletResponse response,
                         @RequestParam("name") String name, @RequestParam("age") Integer age){
        String user = userService.getUser(name, age);
        System.out.println(user);
    }

    @RequestMapping("/getUser")
    public ModelAndView getUser(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("name") String name, @RequestParam("age") Integer age) throws IOException {
        String user = userService.getUser(name, age);
        //System.out.println(user);
        return out(response,user);
    }
    private ModelAndView out(HttpServletResponse resp,String str){
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/first.html")
    public ModelAndView query(@RequestParam("name") String teacher, @RequestParam("age") Integer age){
        String user = userService.getUser(teacher, age);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("name", teacher);
        model.put("data", user);
        model.put("token", "123456");
        return new ModelAndView("first.html",model);
    }
}
