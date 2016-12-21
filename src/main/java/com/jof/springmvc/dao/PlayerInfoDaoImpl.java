package com.jof.springmvc.dao;

import com.jof.springmvc.model.PlayerInfo;
import org.springframework.stereotype.Repository;

/**
 * Created by Ferenc_S on 12/21/2016.
 */
@Repository("playerInfoDao")
public class PlayerInfoDaoImpl extends AbstractDao<Long, PlayerInfo> implements PlayerInfoDao {
    @Override
    public void save(PlayerInfo playerInfo) {
            persist(playerInfo);
    }
}
