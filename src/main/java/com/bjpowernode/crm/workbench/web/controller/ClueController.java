package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.util.DateTimeUtil;
import com.bjpowernode.crm.util.PrintJson;
import com.bjpowernode.crm.util.ServiceFactory;
import com.bjpowernode.crm.util.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入线索控制器");

        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)) {

            getUserList(request,response);

        } else if ("/workbench/clue/save.do".equals(path)) {

            save(request, response);

        }else if ("/workbench/clue/detail.do".equals(path)) {

            detail(request, response);

        }else if ("/workbench/clue/getActivityListById.do".equals(path)) {

            getActivityListById(request, response);

        }else if ("/workbench/clue/deleteById.do".equals(path)) {

            deleteById(request, response);

        }else if ("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)) {

            getActivityListByNameAndNotByClueId(request, response);

        }else if ("/workbench/clue/bund.do".equals(path)) {

            bund(request, response);

        }else if ("/workbench/clue/getActivityListByName.do".equals(path)) {

            getActivityListByName(request, response);

        }else if ("/workbench/clue/convert.do".equals(path)) {

            convert(request, response);

        }

    }

    public void convert(HttpServletRequest request, HttpServletResponse response){

        System.out.println("线索转换操作");

        String clueId = request.getParameter("clueId");

        //接收是否需要创建交易的标记
        String flag = request.getParameter("flag");

        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        Tran tran = null;

        //如果需要创建交易
        if ("a".equals(flag)){

            tran = new Tran();
            //接收交易表单中的数据
            tran.setId(UUIDUtil.getUUID());
            tran.setActivityId(request.getParameter("activityId"));
            tran.setMoney(request.getParameter("money"));
            tran.setName(request.getParameter("name"));
            tran.setExpectedDate(request.getParameter("expectedDate"));
            tran.setStage(request.getParameter("stage"));
            tran.setCreateBy(createBy);
            tran.setCreateTime(DateTimeUtil.getSysTime());

        }

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag1 = cs.convert(clueId,tran,createBy);

        PrintJson.printJsonFlag(response,flag1);

    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {

        String activityName = request.getParameter("activityName");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        List<Activity> activityList = cs.getActivityListByName(activityName);

        PrintJson.printJsonObj(response,activityList);

    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {

        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.bund(cid,aids);

        PrintJson.printJsonFlag(response,flag);

    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {

        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        Map<String,String> map = new HashMap<>();
        map.put("aname",aname);
        map.put("clueId",clueId);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        List<Activity> activityList = cs.getActivityListByNameAndNotByClueId(map);

        PrintJson.printJsonObj(response,activityList);

    }

    private void deleteById(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入删除线索市场关系操作");

        String id = request.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.deleteById(id);

        PrintJson.printJsonFlag(response,flag);

    }

    private void getActivityListById(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("通过clueId查市场活动");

        String id = request.getParameter("clueId");
//        System.out.println(id);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        List<Activity> activityList = cs.getActivityListById(id);

//        System.out.println("市场活动第一条====" +activityList.get(1));

        PrintJson.printJsonObj(response,activityList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Clue clue = cs.detail(id);

        //System.out.println(clue.getFullname());

        request.setAttribute("c",clue);

        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入线索保存操作");

        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue clue = new Clue();

        clue.setId(id);
        clue.setFullname(fullname);
        clue.setAppellation(appellation);
        clue.setOwner(owner);
        clue.setCompany(company);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setMphone(mphone);
        clue.setState(state);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setNextContactTime(nextContactTime);
        clue.setAddress(address);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.save(clue);

        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("获取用户列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = us.getUserList();

        PrintJson.printJsonObj(response,userList);

    }

}
