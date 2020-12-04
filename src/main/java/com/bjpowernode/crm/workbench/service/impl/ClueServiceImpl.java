package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.util.DateTimeUtil;
import com.bjpowernode.crm.util.SqlSessionUtil;
import com.bjpowernode.crm.util.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);


    @Override
    public boolean save(Clue clue) {

        boolean flag = true;

        int result = clueDao.save(clue);

        if (result != 1){

            flag = false;

        }

        return flag;
    }

    @Override
    public Clue detail(String id) {

        Clue clue = clueDao.getClueById(id);

        return clue;
    }

    @Override
    public List<Activity> getActivityListById(String id) {

        List<Activity> activityList = activityDao.getActivityListById(id);

        return activityList;
    }

    @Override
    public boolean deleteById(String id) {

        boolean flag = true;

        int result = clueActivityRelationDao.deleteById(id);

        if (result != 1){

            flag = false;

        }

        return flag;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {

        List<Activity> activityList = activityDao.getActivityListByNameAndNotByClueId(map);

        return activityList;
    }

    @Override
    public boolean bund(String cid, String[] aids) {

        boolean flag = true;

        for(String aid:aids){

            ClueActivityRelation car = new ClueActivityRelation();
            car.setActivityId(aid);
            car.setId(UUIDUtil.getUUID());
            car.setClueId(cid);

            int result = clueActivityRelationDao.bund(car);

            if (result != 1){

                flag = false;

            }
        }

        return flag;
    }

    @Override
    public List<Activity> getActivityListByName(String activityName) {

        List<Activity> activityList = activityDao.getActivityListByName(activityName);

        return activityList;
    }

    @Override
    public boolean convert(String clueId, Tran tran, String createBy) {

        boolean flag = true;

        String createTime = DateTimeUtil.getSysTime();

        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueDao.getClueById(clueId);

        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        Customer customer = customerDao.getCustomerByCompany(clue.getCompany());

        if (customer == null){

            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setOwner(clue.getOwner());
            customer.setName(clue.getCompany());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setContactSummary(clue.getContactSummary());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setAddress(clue.getAddress());

            int count2 = customerDao.save(customer);
            if (count2 != 1){
                flag = false;
            }

        }

        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setOwner(clue.getOwner());
        contacts.setSource(clue.getSource());
        contacts.setCustomerId(customer.getId());
        contacts.setFullname(clue.getFullname());
        contacts.setAppellation(clue.getAppellation());
        contacts.setEmail(clue.getEmail());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setCreateBy(createBy);
        contacts.setCreateTime(createTime);
        contacts.setDescription(clue.getDescription());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setNextContactTime(clue.getNextContactTime());

        int count3 = contactsDao.save(contacts);
        if (count3 != 1){
            flag = false;
        }

        //(4) 线索备注转换到客户备注以及联系人备注
        //根据线索id查询出备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getClueRemarkByClueId(clueId);

        //取出每一条线索备注
        for (ClueRemark clueRemark : clueRemarkList){

            //取出线索
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(customer.getId());
            int count1 = customerRemarkDao.save(customerRemark);
            if (count1 != 1){
                flag = false;
            }

            //创建联系人备注对象，添加联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setEditFlag("0");
            contactsRemark.setContactsId(contacts.getId());
            int count4 = contactsRemarkDao.save(contactsRemark);
            if (count4 != 1){
                flag = false;
            }

        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //通过线索id获取关联的市场活动id
        List<ClueActivityRelation> relationList = clueActivityRelationDao.getListByClueId(clueId);

        for (ClueActivityRelation clueActivityRelation : relationList){

            String activityId = clueActivityRelation.getActivityId();

            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());

            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5 != 1){
                flag = false;
            }

        }

        //(6) 如果有创建交易需求，创建一条交易
        if (tran != null){

            /*
                tran对象在controller中已经封装好的信息
                    id, activityId, money, name, expectedDate, stage. createBy, createTime
            */
            tran.setSource(clue.getSource());
            tran.setContactSummary(clue.getContactSummary());
            tran.setContactsId(contacts.getId());
            tran.setCustomerId(customer.getId());
            tran.setDescription(clue.getDescription());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());

            //添加交易
            int count6 = tranDao.save(tran);
            if (count6 != 1){
                flag = false;
            }

            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            tranHistory.setTranId(tran.getId());

            int cout7 = tranHistoryDao.save(tranHistory);
            if (cout7 != 1){
                flag = false;
            }
        }

        //(8) 删除线索备注
        int count8 = clueRemarkDao.deleteByClueId(clueId);
        if (count8 < 0){
            flag = false;
        }

        //(9) 删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation : relationList) {

            int count9 = clueActivityRelationDao.deleteById(clueActivityRelation.getId());
            if (count9 < 0){
                flag = false;
            }

        }

            //(10) 删除线索
        int count10 = clueDao.deleteById(clueId);
        if (count10 < 0){
            flag = false;
        }

        return flag;
    }
}
