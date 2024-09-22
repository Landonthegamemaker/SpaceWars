package entity;

import components.ShipComponent;
import entity.Ship;
import util.BattleUtil;

import java.util.ArrayList;

public class Team {
    private String teamName;
    private ArrayList<Ship> teamShips;
    private ArrayList<Ship> teamTurnOrder;

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamShips(ArrayList<Ship> teamShips) {
        this.teamShips = teamShips;
    }

    public ArrayList<Ship> getTeamShips() {
        return teamShips;
    }

    public Team(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return this.teamName;
    }
}
