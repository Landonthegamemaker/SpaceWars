package battle;

import components.Weapon;
import entity.Team;
import entity.Ship;
import util.BattleUtil;
import static util.BattleUtil.*;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Choose a random seed for the battle:");
        long battleSeed = scan.nextLong();
        System.out.println("Please provide a name for the first team:");
        scan.nextLine();
        String team1Name = scan.nextLine();

        System.out.println("Please provide a name for the second team:");
        String team2Name = scan.nextLine();

        //Keyboard input for entity.Team 1 Ships
        ArrayList<Ship> team1Ships = new ArrayList<>();
        System.out.println("Type in the names of the ships on the first team and then a q:");

        String shipName = "";

        while (!(shipName.equals("q"))) {

            shipName = scan.nextLine();

            if (shipName.equals("q")) {
                break;
            }
                Ship ship = new Ship(shipName);
                team1Ships.add(ship);
                ship.setName(ship.getName() + " - " + team1Ships.size());
                System.out.println(ship.getName());
                System.out.println(ship.getBaseIntegrity());
                System.out.println(ship.getSpeed());
                System.out.println(ship.getMaxBattery());
                System.out.println(ship.getCurrBattery());
                System.out.println(ship.getBatteryRegen());

        }

        //Keyboard input for entity.Team 2 Ships
        ArrayList<Ship> team2Ships = new ArrayList<>();

        System.out.println("Type in the names of the ships on the second team and then a q:");

        shipName = "";

        while (!(shipName.equals("q"))) {

            shipName = scan.nextLine();

            if (shipName.equals("q")) {
                break;
            }

            Ship ship = new Ship(shipName);
            team2Ships.add(ship);
            ship.setName(ship.getName() + " - " + team2Ships.size());
            System.out.println(ship.getName());
            System.out.println(ship.getBaseIntegrity());
            System.out.println(ship.getSpeed());
            System.out.println(ship.getMaxBattery());
            System.out.println(ship.getCurrBattery());
            System.out.println(ship.getBatteryRegen());

        }

            System.out.println("Are you ready for battle? (Y/N)");
            String output = scan.nextLine();
            output = output.toLowerCase();

        if (output.equals("y")) {
            System.out.println("Prepare for battle!");

            Random randomGen = new Random(battleSeed);
            Team team1 = new Team(team1Name);
            team1.setTeamShips(team1Ships);
            Team team2 = new Team(team2Name);
            team2.setTeamShips(team2Ships);

            battle(team1,team2,randomGen);

        }
    }
}
