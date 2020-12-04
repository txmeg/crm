package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.util.MD5Util;
import com.bjpowernode.crm.util.PrintJson;
import com.bjpowernode.crm.util.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户控制器");

        String path = request.getServletPath();

        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if ("/settings/user/xxx.do".equals(path)){

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        String ip = request.getRemoteAddr();
        System.out.println("--------------ip" + ip);

        loginPwd = MD5Util.getMD5(loginPwd);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try {
            User user = us.login(loginAct,loginPwd,ip);

            request.getSession().setAttribute("user",user);

            PrintJson.printJsonFlag(response,true);
        } catch (Exception e) {
            e.printStackTrace();

            //一旦程序执行了catch快的信息，说明业务层为我们验证登录失败，为controller抛出了异常
            //表示登录失败
            /*
                {"success":false,"msg":?}
            */
            String msg = e.getMessage();
            System.out.println(e);
            /*
                我们现在作为controller，需要为ajax请求提供多项信息

                可以有两种手段来处理：
                    1）将多项信息打包成map，将map解析为json串
                    2）创建一个vo
                        private boolean success；
                        private String msg；

                如果对于展现的信息将来还会大量使用，我们创建一个vo类，使用方便
                如果对于展现的信息只有在这个需求中能够使用，我们使用map就可以了
             */
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("success",false);
            map.put("msg",msg);

            PrintJson.printJsonObj(response,map);
        }

    }

}
