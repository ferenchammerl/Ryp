package com.jof.springmvc.service;

import com.jof.springmvc.model.Champion;
import com.jof.springmvc.model.Match;
import com.jof.springmvc.model.MatchPlayer;
import com.jof.springmvc.model.PlayerInfo;
import com.jof.springmvc.util.region.InvalidRegionException;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.Region;
import net.rithms.riot.dto.Game.Game;
import net.rithms.riot.dto.Game.Player;
import net.rithms.riot.dto.Game.RecentGames;
import net.rithms.riot.dto.Summoner.RunePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.util.*;

/**
 * Created by Ferenc_S on 12/10/2016.
 */
@Service("riotApiService")
@Profile("prod")
public class RiotApiServiceImpl implements RiotApiService {

    private final Environment environment;
    RiotApi riotApi;
    MatchService matchService;

    @Autowired
    PlayerInfoService playerInfoService;

    @Autowired
    public RiotApiServiceImpl(Environment environment, MatchService matchService) {
        this.environment = environment;
        this.matchService = matchService;
    }

    public RiotApiServiceImpl(Environment environment, RiotApi riotApi, MatchService matchService) {
        this.environment = environment;
        this.riotApi = riotApi;
        this.matchService = matchService;
    }

    @PostConstruct
    private void init() {
        this.riotApi = new RiotApi(environment.getRequiredProperty("riot.api.key"));
        this.riotApi.setRegion(getRegion());
    }

    @Override
    public long getSummonerIdByName(String summonerName) throws RiotApiException {
        return riotApi.getSummonerByName(summonerName).getId();
    }

    @Override
    public boolean userHasRunePage(long id, String runePageName) throws RiotApiException {
        Set<RunePage> pages = riotApi.getRunePages(id).getPages();
        for (RunePage page : pages) {
            if (page.getName().equals(runePageName)) return true;
        }
        return false;
    }

    @Override
    public List<Match> getRecentGames(Long id, String summonerName) throws RiotApiException {
        RecentGames recentGames = riotApi.getRecentGames(id);
        Set<Game> games = recentGames.getGames();
        List<Match> matches = new ArrayList<>();

        List ids = new ArrayList<>();
        for (Game game : games) {
            for (Player p : game.getFellowPlayers()) {
                ids.add(p.getSummonerId());
            }
        }
        long[] longIds = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            longIds[i] = (long) ids.get(i);
        }

        Map<String, String> summonerNames = new HashMap<>();
        for (int i = 0; i < longIds.length; i += 39) {
            int to = i + Math.min(longIds.length - i, 39);
            long[] summonerIds = Arrays.copyOfRange(longIds, i, to);
            Map<String, String> temp = riotApi.getSummonerNames(summonerIds);
            summonerNames.putAll(temp);
        }
        for (Game game : games) {
            Match match = matchService.findById(game.getGameId());
            if (match != null) {
                matches.add(match);
                continue;
            }
            match = new Match();
            match.setId(game.getGameId());
            match.setCreated_at(new Date(game.getCreateDate()));

            List<MatchPlayer> matchPlayers = new ArrayList<>();
            MatchPlayer matchPlayer = new MatchPlayer();
            matchPlayer.setMatch(match);
            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.setSummonerId(recentGames.getSummonerId());
            playerInfo.setSummonerName(summonerName);
            matchPlayer.setPlayerInfo(playerInfo);
            matchPlayer.setTeamId(game.getStats().getTeam());

            Champion champion = new Champion();
            champion.setId(game.getChampionId());
            matchPlayer.setChampion(champion);
            matchPlayers.add(matchPlayer);
            matchPlayer.setTeamId(game.getTeamId());
            match.setWinnerTeamId(game.getStats().isWin() ? matchPlayer.getTeamId() : theOppositeTeam(matchPlayer.getTeamId()));

            for (Player p : game.getFellowPlayers()) { // playerinfos
                matchPlayer = new MatchPlayer();
                matchPlayer.setMatch(match);
                playerInfo = new PlayerInfo();
                playerInfo.setSummonerId(p.getSummonerId());
                playerInfo.setSummonerName(summonerNames.get(String.valueOf(p.getSummonerId())));
                matchPlayer.setPlayerInfo(playerInfo);
                champion = new Champion();
                champion.setId(p.getChampionId());
                matchPlayer.setChampion(champion);
                matchPlayer.setTeamId(p.getTeamId());
                matchPlayers.add(matchPlayer);
            playerInfoService.saveAll(playerInfo);
            }
            Collections.sort(matchPlayers);
            match.setMatchPlayers(matchPlayers);
            matches.add(match);
        }

        Collections.sort(matches);
        Collections.reverse(matches); // newest / biggest number first, to the top
        return matches;
    }

    private int theOppositeTeam(int team_id) {
        return team_id == BLUE_TEAM_ID ? RED_TEAM_ID : BLUE_TEAM_ID;
    }

    private Region getRegion() {
        switch (environment.getRequiredProperty("riot.api.region")) {
            case "EUNE":
                return Region.EUNE;
            case "EUW":
                return Region.EUW;
            default:
                throw new InvalidRegionException("No such region implemented yet!");
        }
    }
}
