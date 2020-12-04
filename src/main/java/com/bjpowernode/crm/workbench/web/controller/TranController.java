package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.util.DateTimeUtil;
import com.bjpowernode.crm.util.PrintJson;
import com.bjpowernode.crm.util.ServiceFactory;
import com.bjpowernode.crm.util.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入交易控制器");

        String path = request.getServletPath();

        if ("/workbench/transaction/add.do".equals(path)) {

            getUserList(request,response);

        } else if ("/workbench/transaction/getCustomerName.do".equals(path)) {

            getCustomerName(request, response);

        }else if ("/workbench/transaction/save.do".equals(path)) {

            save(request, response);

        }else if ("/workbench/transaction/detail.do".equals(path)) {

            detaill(request, response);

        }else if ("/workbench/transaction/getTranListByTranId.do".equals(path)) {

            getTranListByTranId(request, response);

        }else if ("/workbench/transaction/changeStage.do".equals(path)) {

            changeStage(request, response);

        }else if ("/workbench/transaction/getCharts.do".equals(path)) {

            getCharts(request, response);

        }

    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Map<String,Object> map = ts.getCharts();

        PrintJson.printJsonObj(response,map);

    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("修改交易阶段操作");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        String possibility = ((Map<String,String>)request.getServletContext().getAttribute("pMap")).get(stage);
        t.setPossibility(possibility);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.changeStage(t);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("t",t);

        PrintJson.printJsonObj(response,map);

    }

    private void getTranListByTranId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("获取交易历史列表");

        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> tranHistoryList = ts.getTranListByTranId(tranId);

        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");

        for (TranHistory tranHistory : tranHistoryList){

            String possibility = pMap.get(tranHistory.getStage());

            tranHistory.setPossibility(possibility);

        }

        PrintJson.printJsonObj(response,tranHistoryList);

    }

    private void detaill(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("跳转到交易详细页面");

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran t = ts.detail(id);

        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(t.getStage());
        t.setPossibility(possibility);

        request.setAttribute("t",t);

        //跳转到新的页面需要用到请求作用域，所以用请求转发跳转页面
        //页面停留在detail.do，刷新会获取最新的数据
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("执行交易添加操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();

        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(tran,customerName);

        if (flag){

            //如果是请求转发，会让地址停留在save.do，所以这里用重定向，打开一个新的界面
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");

        }

    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得客户名称列表（按照客户名称进行模糊查询）");

        String name = request.getParameter("name");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> nameList = cs.getCustomerName(name);

        PrintJson.printJsonObj(response,nameList);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到跳转到添加页的操作");

        UserService us = (UserService)ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = us.getUserList();

        request.setAttribute("userList",userList);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);

    }


}
