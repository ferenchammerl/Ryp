package com.jof.springmvc.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ferenc_S on 12/15/2016.
 */
@Entity
@Table(name = "game") // Not match; MySQL is not exceptionally happy when one tries to name a table a keyword
public class Match implements Serializable, Comparable {
    @Id
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private Date created_at;


    // So that we don't violate the first normal form by stashing 10 player Ids in a single column
    @OneToMany(mappedBy = "match", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<MatchPlayer> matchPlayers = new ArrayList<MatchPlayer>();

    @Column(name = "winner_team_id", nullable = false)
    private Integer winnerTeamId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public List<MatchPlayer> getMatchPlayers() {
        return matchPlayers;
    }

    public void setMatchPlayers(List<MatchPlayer> matchPlayers) {
        this.matchPlayers = matchPlayers;
    }

    public int getWinnerTeamId() {
        return winnerTeamId;
    }

    public void setWinnerTeamId(int winnerTeamId) {
        this.winnerTeamId = winnerTeamId;
    }

    public boolean didPlayerWin(Long playerId) {
        for (MatchPlayer matchPlayer : matchPlayers) {
            if (matchPlayer.getSummonerId().equals(playerId)) {
                return matchPlayer.getTeamId() == winnerTeamId;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Match)) {
            return 0;
        }
        Match match = (Match) o;
        if (this.created_at.compareTo(match.created_at) < 0) {
            return -1;
        } else if (this.created_at.compareTo(match.created_at) > 0) {
            return 1;
        }
        return 0;
    }
}
