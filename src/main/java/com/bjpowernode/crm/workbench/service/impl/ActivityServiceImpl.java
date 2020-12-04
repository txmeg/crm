package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.util.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityServiceImpl implements ActivityService {

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = (UserDao) SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean save(Activity activity) {

        boolean flag = true;

        int result = activityDao.save(activity);

        if (result != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public PaginationVo<Activity> pageList(Map<String, Object> map) {

        List<Activity> dataList = activityDao.getActivityListByCondition(map);

        int total = activityDao.getTotalByCondition(map);

        PaginationVo<Activity> pv = new PaginationVo<>();
        pv.setDataList(dataList);
        pv.setTotal(String.valueOf(total));

        return pv;
    }

    @Override
    public boolean delete(String[] ids) {

        boolean flag = true;

        //查询出需要删除备注的数量
        int count = activityRemarkDao.getCountByAids(ids);

        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = activityRemarkDao.deleteByAids(ids);

        if (count != count2){

            flag = false;

        }

        //删除活动记录
        int count3 = activityDao.deleteByAids(ids);

        if (count3 != ids.length){

            flag = false;

        }

        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {

        //取得userList
        List<User> userList = userDao.getUserList();

        //取得activity
        Activity activity = activityDao.getById(id);

        //打包
        Map<String,Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("activity",activity);

        //返回
        return map;
    }

    @Override
    public boolean update(Activity activity) {

        boolean flag = true;

        int result = activityDao.update(activity);

        if (result != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Activity detail(String id) {

        Activity activity = activityDao.detail(id);

        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String aid) {

        List<ActivityRemark> activityRemarkList = activityRemarkDao.getRemarkListByAid(aid);

        return activityRemarkList;
    }

    @Override
    public boolean deleteRemarkById(String id) {

        boolean flag = true;

        int result = activityRemarkDao.deleteRemarkById(id);

        if(result != 1){

            flag = false;

        }

        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {

        boolean flag = true;

        int result = activityRemarkDao.saveRemarkByAid(activityRemark);

        if(result != 1){

            flag = false;

        }

        return flag;
    }

    @Override
    public boolean editRemark(ActivityRemark activityRemark) {

        boolean flag = true;

        int result = activityRemarkDao.editRemark(activityRemark);

        if(result != 1){

            flag = false;

        }

        return flag;
    }
}
