package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.util.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        System.out.println("数据字典缓存开始");

        ServletContext application = servletContextEvent.getServletContext();

        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        Map<String, List<DicValue>> map = ds.getAll();

        /*
            map
                ("",dicValue)
        */

        Set<String> keySet = map.keySet();

        for (String key : keySet){

            application.setAttribute(key,map.get(key));

        }

        System.out.println("数据字典缓存结束");

        System.out.println("properties文件开始加载");

        Map<String,String> pMap = new HashMap<>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> e = rb.getKeys();

        while(e.hasMoreElements()){

            String key = e.nextElement();
            String value = rb.getString(key);

            pMap.put(key,value);
        }

        application.setAttribute("pMap",pMap);

        System.out.println("properties文件加载完成");

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
