package com.jof.springmvc.service;

import com.jof.springmvc.dao.PlayerInfoDao;
import com.jof.springmvc.model.PlayerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Ferenc_S on 12/21/2016.
 */
@Service("playerInfoService")
public class PlayerInfoServiceImpl implements PlayerInfoService {

    @Autowired
    PlayerInfoDao playerInfoDao;

    @Override
    public void saveAll(PlayerInfo c) {
        playerInfoDao.save(c);
    }
}
