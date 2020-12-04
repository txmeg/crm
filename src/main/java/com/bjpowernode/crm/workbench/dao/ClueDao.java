package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Clue;

public interface ClueDao {


    int save(Clue clue);

    Clue getClueById(String id);

    int deleteById(String clueId);
}
