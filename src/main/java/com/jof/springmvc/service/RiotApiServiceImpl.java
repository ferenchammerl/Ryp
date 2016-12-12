package com.jof.springmvc.service;

import java.util.Map;
import java.util.Set;

import net.rithms.riot.constant.Region;
import net.rithms.riot.dto.Summoner.Rune;
import net.rithms.riot.dto.Summoner.RunePage;
import net.rithms.riot.dto.Summoner.RunePages;
import net.rithms.riot.dto.Summoner.Summoner;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by Ferenc_S on 12/10/2016.
 */
@Service("riotApiService")
public class RiotApiServiceImpl implements RiotApiService {

    private final Environment environment;

    RiotApi riotApi;

    @Autowired
    public RiotApiServiceImpl(Environment environment) {
        this.environment = environment;
    }

    public RiotApiServiceImpl(Environment environment, RiotApi riotApi) {
        this.environment = environment;
        this.riotApi = riotApi;
    }

    @PostConstruct
    private void init() {
        // TODO: find a better way to instatiate it
        this.riotApi = new RiotApi(environment.getRequiredProperty("riot.api.key"));

    }

    @Override
    public boolean userHasRunePage(Region region, long id, final String runePageName) throws RiotApiException {
        Set<RunePage> pages = riotApi.getRunePages(region, id).getPages();
        for (RunePage page : pages) {
            if (page.getName().equals(runePageName)) return true;
        }
        return false;
    }
}
