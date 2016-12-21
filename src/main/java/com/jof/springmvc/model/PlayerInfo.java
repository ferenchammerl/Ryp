package com.jof.springmvc.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

/**
 * Created by Ferenc_S on 12/21/2016.
 */
@Entity
@Table(name = "playerinfo")
public class PlayerInfo {
    /*
    No need for composite primary key, summoner_id itself guarantees
     */
    @Id
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Column(name = "summoner_id", nullable = false)
    private Long summonerId;

    @NotEmpty
    @Column(name = "summoner_name", nullable = false)
    private String summonerName;

    @OneToOne
    @PrimaryKeyJoinColumn
    private User user;

    public PlayerInfo() {
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public Long getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(Long summonerId) {
        this.summonerId = summonerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
