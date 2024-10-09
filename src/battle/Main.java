package battle;
import components.Weapon;
import entity.Team;
import entity.Ship;
import util.BattleUtil;
import static util.BattleUtil.*;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.DoubleStream;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Choose a random seed for the battle:");
        long seed = scan.nextLong();
        System.out.println("Please provide a name for the first team:");
        scan.nextLine();
        String team1Name = scan.nextLine();
        System.out.println("Please provide a name for the second team:");
        String team2Name = scan.nextLine();

        Team team1 = new Team(team1Name);
        System.out.println("Type in the names of the ships on the first team and then a q:");
        ArrayList<Ship> team1Ships = team1.scanShips(team1, scan);

        Team team2 = new Team(team2Name);
        System.out.println("Type in the names of the ships on the second team and then a q:");
        ArrayList<Ship> team2Ships = team2.scanShips(team2, scan);

            System.out.println("Are you ready for battle? (Y/N)");
            String output = scan.nextLine();
            output = output.toLowerCase();

        if (output.equals("y")) {
            System.out.println("Prepare for battle!");

            Random numGenerator = new Random(seed);
            team1.setTeamShips(team1Ships);
            team2.setTeamShips(team2Ships);

            setIsBattleComplete(false);

            turnNum = 1;

            while (turnNum <= getMAX_NUMBER_OF_TURNS()) {
                battle(team1, team2, numGenerator);


                if (getIsBattleComplete()) {
                    break;
                }
            }

            if (team2.getTeamShips().isEmpty()) {
                System.out.println(team1.getTeamName() + " has won the battle. " + team2.getTeamName() + " suffers ignominious defeat.");
            }
            else if (team1.getTeamShips().isEmpty()) {
                System.out.println(team2.getTeamName() + " has won the battle. " + team1.getTeamName() + " suffers ignominious defeat.");
            }
            else {
                System.out.println("Exhausted from battle, all ships flee seeking repairs.");
            }
        }
    }
}