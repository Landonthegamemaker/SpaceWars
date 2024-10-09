package entity;

import components.ShipComponent;
import entity.Ship;
import util.BattleUtil;
import java.util.ArrayList;
import java.util.Scanner;

public class Team {
    private String teamName;
    private ArrayList<Ship> teamShips;

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
    public ArrayList<Ship> scanShips(Team team, Scanner scan) {
        ArrayList<Ship> ships = new ArrayList<>();
        String shipName = "";

        while (!(shipName.equals("q"))) {

            shipName = scan.nextLine();

            if (shipName.equals("q")) {
                break;
            }
            Ship ship = new Ship(shipName);
            ship.setTeamName(teamName);
            ship.setTeam(team);
            ships.add(ship);
            ship.setName(ship.getName() + " - " + ships.size());
        }
        return ships;
    }
    @Override
    public String toString() {
        return this.teamName;
    }
}
