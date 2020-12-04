package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.util.SqlSessionUtil;
import com.bjpowernode.crm.util.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tran, String customerName) {

        /*
            交易添加业务：
                在做添加之前，参数t里面就少了一项信息，就是客户的主键，customerId

                先处理客户相关信息
                    1）判断customerName，根据客户名称在客户表进行精确查询
                        如果有这个客户，则取出这个客户的id，封装到t对象中
                        如果没有这个客户，则在客户表中新建一条客户信息，然后将新建的客户的id取出，封装到t对象中

                    2）经过以上操作后，t对象中的信息就全了，需要执行添加交易的操作

                    3）添加交易完毕之后，需要创建一条交易历史
         */

        boolean flag = true;

        Customer cus = customerDao.getCustomerByCompany(customerName);

        if (cus == null) {

            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(tran.getCreateBy());
            cus.setCreateTime(tran.getCreateTime());
            cus.setOwner(tran.getOwner());
            cus.setNextContactTime(tran.getNextContactTime());
            cus.setContactSummary(tran.getContactSummary());
            cus.setDescription(tran.getDescription());

            int count1 = customerDao.save(cus);
            if (count1 != 1){
                flag = false;
            }
        }

        //添加交易
        tran.setCustomerId(cus.getId());

        int count2 = tranDao.save(tran);
        if (count2 != 1){
            flag = false;
        }

        //添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setCreateTime(tran.getCreateTime());

        int count3 = tranHistoryDao.save(tranHistory);
        if (count3 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Tran detail(String id) {

        Tran t = tranDao.detail(id);

        return t;
    }

    @Override
    public List<TranHistory> getTranListByTranId(String tranId) {

        List<TranHistory> tranHistoryList = tranHistoryDao.getTranListByTranId(tranId);

        return tranHistoryList;
    }

    @Override
    public boolean changeStage(Tran t) {

        boolean flag = true;

        int count1 = tranDao.changeStage(t);
        if (count1 != 1){
            flag = false;
        }

        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(t.getId());
        tranHistory.setCreateTime(t.getEditTime());
        tranHistory.setCreateBy(t.getEditBy());
        tranHistory.setExpectedDate(t.getExpectedDate());
        tranHistory.setMoney(t.getMoney());
        tranHistory.setStage(t.getStage());
        tranHistory.setPossibility(t.getPossibility());

        int count2 = tranHistoryDao.save(tranHistory);
        if (count2 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {

        //取得记录总数
        int total = tranDao.getTotal();

        //取得dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();

        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);

        return map;
    }
}
