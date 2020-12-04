package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean save(Clue clue);

    Clue detail(String id);

    List<Activity> getActivityListById(String id);

    boolean deleteById(String id);

    List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map);

    boolean bund(String cid, String[] aids);

    List<Activity> getActivityListByName(String activityName);

    boolean convert(String clueId, Tran tran, String createBy);
}
