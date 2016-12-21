package com.jof.springmvc.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

/**
 * Created by Ferenc_S on 12/15/2016.
 * Stores data about a player's actions in a particular match
 */
@Entity
@Table(name = "matchPlayer")
public class MatchPlayer implements Comparable<MatchPlayer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PLAYER_MATCH_ID"))
    private Match match;

    @ManyToOne(optional = false)
    @JoinColumn(name = "champion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PLAYER_CHAMPION_ID"))
    private Champion champion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "playerinfo_id", foreignKey = @ForeignKey(name = "FK_PLAYER_INFO_ID"))
    private PlayerInfo playerInfo;
    /*
     *
     * instead of isWinner, we store team ID and game stores winnerTeamId
     */
    @Column(name = "teamId", nullable = false)
    private int teamId;

    public Long getSummonerId() {
        return playerInfo.getSummonerId();
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Champion getChampion() {
        return champion;
    }

    public void setChampion(Champion champion) {
        this.champion = champion;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getSummonerName() {
        return playerInfo.getSummonerName();
    }


    @Override
    public int compareTo(MatchPlayer that) {
        if (this.teamId < that.teamId) {
            return -1;
        } else if (this.teamId > that.teamId) {
            return 1;
        }
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }
}
