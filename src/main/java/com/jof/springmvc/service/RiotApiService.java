package com.jof.springmvc.service;

import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.Region;

/**
 * Created by Ferenc_S on 12/10/2016.
 */
public interface RiotApiService {

    boolean userHasRunePage(Region region, long id, String runePageName) throws RiotApiException;
}
