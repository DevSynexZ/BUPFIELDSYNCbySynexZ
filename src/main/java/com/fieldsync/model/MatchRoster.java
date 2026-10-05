package com.fieldsync.model;

import java.util.ArrayList;
import java.util.List;

public class MatchRoster {
    private int rosterId;
    private int reservationId;
    private String captainName;
    private String managerName;
    private List<String> players;

    public MatchRoster() {
        this.players = new ArrayList<>();
    }

    public MatchRoster(int rosterId, int reservationId, String captainName, String managerName, List<String> players) {
        this.rosterId = rosterId;
        this.reservationId = reservationId;
        this.captainName = captainName;
        this.managerName = managerName;
        this.players = players != null ? players : new ArrayList<>();
    }

    public int getRosterId() {
        return rosterId;
    }

    public void setRosterId(int rosterId) {
        this.rosterId = rosterId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getCaptainName() {
        return captainName;
    }

    public void setCaptainName(String captainName) {
        this.captainName = captainName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}