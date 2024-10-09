package util;
import components.*;
import entity.Ship;
import entity.Team;

import java.lang.reflect.Array;
import java.util.*;

public abstract class BattleUtil {

    public static int turnNum;
    private static boolean isBattleComplete;
    private static final int MAX_NUMBER_OF_TURNS = 20;

    public static int getMAX_NUMBER_OF_TURNS() {
        return MAX_NUMBER_OF_TURNS;
    }

    public static boolean getIsBattleComplete() {
        return isBattleComplete;
    }

    public static void setIsBattleComplete(boolean isBattleComplete) {
        BattleUtil.isBattleComplete = isBattleComplete;
    }

    public static void battle(Team team1, Team team2, Random numGenerator) {

        while (!getIsBattleComplete() && turnNum <= getMAX_NUMBER_OF_TURNS()) {

            System.out.println("Turn " + turnNum + " has begun!");

            if (!team1.getTeamShips().isEmpty() && !team2.getTeamShips().isEmpty()) {

                ArrayList<Ship> turnOrder = generateShipReactions(team1.getTeamShips(), team2.getTeamShips(), numGenerator);
                teamTurn(team1.getTeamShips(), team2.getTeamShips(),turnOrder, numGenerator);
                turnNum++;

            } else {
                setIsBattleComplete(true);
                break;
            }
        }
    }

    public static ArrayList<Ship> generateShipReactions(ArrayList<Ship> team1Ships, ArrayList<Ship> team2Ships, Random numGenerator) {
        //Uses Random class to generate ship reaction between 0 and its speed (exclusive)
        double reaction = 0.0;
        ArrayList<Double> uniqueReactions = new ArrayList<>();
        HashMap<Double, Ship> hash = new HashMap<>();

        ArrayList<Ship> overallTurnOrder = new ArrayList<>();
        for(Ship ship: team1Ships) {
            if (!ship.isDestroyed()) {
                overallTurnOrder.add(ship);
            }
        }
        for(Ship ship: team2Ships) {
            if (!ship.isDestroyed()) {
                overallTurnOrder.add(ship);
            }
        }

        for (Ship ship : overallTurnOrder) {
            do {
                reaction = numGenerator.nextDouble(0, ship.getSpeed());
            }
            while (uniqueReactions.contains(reaction));
            {
                ship.setReaction(reaction);
                uniqueReactions.add(reaction);
                hash.put(reaction, ship);
            }
        }

        //For Each loop on keyset, Add each element to an ArrayList

        Collections.sort(uniqueReactions, Comparator.comparing(Double::floatValue).reversed());
        ArrayList<Ship> turnOrder = new ArrayList<>();

        //For Each in that ArrrayList, add to another ArrayList of ships
        //HashMap.get(key) from elements of first ArrrayList

        for (Double key : uniqueReactions) {
            turnOrder.add(hash.get(key));
        }

        return turnOrder;
    }

    public static void teamTurn(ArrayList<Ship> team1Ships, ArrayList<Ship> team2Ships, ArrayList<Ship> turnOrder, Random numGenerator) {
        for (int actingShip = 0; actingShip < turnOrder.size(); actingShip++) {
            Ship attacker = turnOrder.get(actingShip);
            ArrayList<Weapon> weapons = new ArrayList<>();
            System.out.println("\t" + attacker.getTeamName() + "’s " + attacker.getName() + " ominously maneuvers into firing position.");
            ArrayList<Ship> shipList = new ArrayList<>();
            if (!team1Ships.contains(attacker)) {
                shipList.addAll(team1Ships);
            }
            else {
                shipList.addAll(team2Ships);
            }
            for (Ship ship:shipList) {
                if (ship.isDestroyed()) {
                    shipList.remove(ship);
                }
            }
            Ship target = targetShip(shipList, numGenerator);
            if (target == null) {
                setIsBattleComplete(true);
                break;

            } else {
                beginTurn(attacker);
                fireAllWeapons(attacker, target, numGenerator);
            }
        }
    }

    public static void beginTurn(Ship ship) {
        ArrayList<Defence> shipComponents = searchForDefences(ship.getComponents());

        //Replenishing Shield
        if (!shipComponents.isEmpty()) {
            for (Defence component : shipComponents) {
                if (component instanceof Shield) {
                    Shield shield = (Shield) component;
                    int increaseInt = (int) (shield.getCurrIntegrity() + shield.getRegenRate());
                    shield.setPassThrough(false);

                    if (shield.getCurrIntegrity() < shield.getMaxIntegrity()) {
                        shield.setCurrIntegrity(increaseInt);
                    }
                    if (shield.getCurrIntegrity() >= shield.getMaxIntegrity()) {

                        shield.setCurrIntegrity(shield.getMaxIntegrity());
                    }
                }
            }
        }

        //Recharging Battery
        int rechargeRate = ship.getBatteryRegen();
        int remainingbattery = ship.getMaxBattery() - ship.getCurrBattery();

        if (ship.getCurrBattery() < ship.getMaxBattery()) {
            ship.setCurrBattery(ship.getCurrBattery() + rechargeRate);
        }
        if (rechargeRate > remainingbattery) {
            ship.setCurrBattery(ship.getMaxBattery());
        }

        ship.sortWeapons(ship.getComponents());
    }

    public static Ship targetShip(ArrayList<Ship> opponentShips, Random numGenerator) {
        ArrayList<Ship> remainingEnemies = opponentShips;
        for (Ship enemy : remainingEnemies) {
            if (enemy.isDestroyed()) {
                remainingEnemies.remove(enemy);
            }
        }
        if (remainingEnemies.isEmpty()) {
            return null;
        }
        return remainingEnemies.get(numGenerator.nextInt(0, remainingEnemies.size()));
    }

    public static double rollOneToHundred(Random numGenerator) {
        //Generates a random floating-point number between 0 and 100 (exclusive)
        double percentCalculated = numGenerator.nextDouble(0, 100);
//        System.out.print("Random: " + percentCalculated);
        return percentCalculated;
    }

    public static int[] roll2d6(Random numGenerator) {
        //Rolls 2 6-sided die to generate 2 random numbers between 1 and 6 (inclusive)

        int die1 = numGenerator.nextInt(1, 7);
        int die2 = numGenerator.nextInt(1, 7);
        int[] dieVals = {die1, die2};
        return dieVals;
    }

    //Readying Weapons

    public static void fireAllWeapons(Ship attacker, Ship defender, Random numGenerator) {

            if (!getIsBattleComplete()) {
                ArrayList<ShipComponent> attackerComponents = attacker.getComponents();
                ArrayList<Weapon> weapons = attacker.getWeapons();
                ArrayList<ShipComponent> defenderComponents = defender.getComponents();
                ArrayList<Weapon> defenderWeapons = defender.getWeapons();
                ArrayList<Defence> defences = searchForDefences(defenderComponents);
                attacker.setWeight(attackerComponents);
                defender.setWeight(defenderComponents);

                for (Weapon weapon: weapons) {
                    double accuracy = weapon.getAccuracy();
                for (int fired = 0; fired < weapon.getFireRate(); fired++) {
                    int damageRemaining = weapon.getDamage();
                    double accuracyBoost = 0.0;
                    Sensor sensor = searchForSensor(attackerComponents);

                    if (sensor != null && !sensor.getisDestroyed()) {
                        accuracyBoost = sensor.getAccuracyBoost();
                    }

                    if (weapon instanceof Laser) {
                       if (!weapon.getisDestroyed()) {
                           int cost = ((Laser) weapon).getBatteryCost();

                           if (cost <= attacker.getCurrBattery()) {
                               int drainBattery = (attacker.getCurrBattery() - cost);
                               attacker.setCurrBattery(drainBattery);
//                               if (attacker.getName().equals("Zoomer - 2") && weapon.getName().equals("Small Heated Cannon")) {
//                                   System.out.println(drainBattery); }
                           } else {break;}
                       }
                       else {break;}
                    }
                    else if (weapon instanceof Railgun) {

                        if (!weapon.getisDestroyed()) {

                            int ammo = ((Railgun) weapon).getAmmo();

                            if (ammo > 0) {

                                ammo--;
                                ((Railgun) weapon).setAmmo(ammo);
//                                if (attacker.getName().equals("Zoomer - 2") && weapon.getName().equals("Small Heated Cannon")) {
//                                    System.out.println(ammo);
//                                }

                            } else {break;}
                        }
                        else {break;}
                    }
                    System.out.println("\t\t" + attacker.getTeamName() + "’s " + attacker.getName() + " fires its " + weapon.getName() + " at " + defender.getTeamName() + "’s " + defender.getName() + " …");


                    if (isAttackDodged(defender, rollOneToHundred(numGenerator))) {
                        System.out.println("\t\t\tbut the " + defender.getTeamName() + "’s " + defender.getName() + " deftly avoids the blow.");
//                        break;
                    }
                    //The weapon hits if a random floating-point number from 0 to 100 (exclusive) is lower than its accuracy plus the ship’s sensors accuracy
                    else if (rollOneToHundred(numGenerator) < (accuracy + accuracyBoost)) {

                        ShipComponent randomComponent;
                        Shield shield = new Shield();
                        Armor armor = new Armor();

                        if (isCriticalStrike(numGenerator)) {
                            //On a critical strike, a random component of the hit ship takes structural integrity damage, without regard to any defensive resistances of the hit ship.
                            //Hit a random component with random index from the xml order
                            damageRemaining = damageRemaining * 2;
                            randomComponent = randomComponentHit(defenderComponents, damageRemaining, numGenerator);

                            randomComponent.setCurrIntegrity(randomComponent.getCurrIntegrity() - damageRemaining);
                            hitOutput(defender, randomComponent.getName(), getDamageType(weapon), damageRemaining, true);

                            if (randomComponent == null) {
                                String damageType = getDamageType(weapon);
                                hullHit(defender, damageType, damageRemaining, true);
                            }

                            if (randomComponent.getCurrIntegrity() <= 0) {

                                if (randomComponent instanceof Shield) {
                                    System.out.println("\t\t\tThe " + randomComponent.getName() + " dissipates.");
                                    randomComponent.setCurrIntegrity(0);
                                    shield.setPassThrough(true);
                                }
                                else {
                                    System.out.println("\t\t\tThe " + randomComponent.getName() + " is destroyed.");
                                        randomComponent.setisDestroyed(true);
                                }
                                if (defender.isDestroyed()) {
                                    System.out.println("\t" + defender.getTeamName() + "’s " + defender.getName() + " EXPLODES in a shower of sparks and fire, lost forever to the inky void!");
                                    ArrayList<Ship> remainingShips = defender.getTeam().getTeamShips();
                                    remainingShips.remove(defender);
                                    defender.getTeam().setTeamShips(remainingShips);

                                    if (defender.getTeam().getTeamShips().isEmpty()) {
                                        setIsBattleComplete(true);
                                        break;
                                    }
                                }
                            }
                        }
                        //Regular Hit
                        else {
                           for (Defence component:defences) {

                                if (component instanceof Shield) {
                                    shield = (Shield) component;

                                    if (!shield.getPassThrough()) {
                                        int actualDamage = getActualDamage(getDamageType(weapon), damageRemaining, shield);
                                        damageRemaining = actualDamage - shield.getCurrIntegrity();

                                        if (actualDamage >= shield.getCurrIntegrity()) {
                                            actualDamage = shield.getCurrIntegrity();
                                            shield.setCurrIntegrity(0);
                                            shield.setPassThrough(true);
                                        }
                                        else {
                                            shield.setCurrIntegrity(shield.getCurrIntegrity() - actualDamage);
                                            damageRemaining = 0;
                                        }
                                        if (actualDamage > 0) {
                                            hitOutput(defender, shield.getName(), getDamageType(weapon), actualDamage, false);
                                        }
                                    }
                                }
                                if (damageRemaining > 0) {
                                    if (component instanceof Armor) {
                                        armor = (Armor) component;

                                        if (!armor.getisDestroyed()) {
                                            int actualDamage = getActualDamage(getDamageType(weapon), damageRemaining, armor);
                                            damageRemaining = actualDamage - armor.getCurrIntegrity();

                                            if (actualDamage >= armor.getCurrIntegrity()) {
                                                actualDamage = armor.getCurrIntegrity();
                                                armor.setCurrIntegrity(0);
                                                armor.setisDestroyed(true);
                                            }
                                            else {
                                                armor.setCurrIntegrity(armor.getCurrIntegrity()-actualDamage);
                                                damageRemaining = 0;
                                                hitOutput(defender, armor.getName(), getDamageType(weapon), actualDamage, false);
                                            }
                                        }
                                    }
                                }
                           }
                            if (shield.getPassThrough() && armor.getisDestroyed()) {
                                if (damageRemaining > 0) {

                                    String damageType = getDamageType(weapon);
                                    hullHit(defender, damageType, damageRemaining, false);

                                    if (defender.isDestroyed()) {

                                        System.out.println("\t" + defender.getTeamName() + "’s " + defender.getName() + " EXPLODES in a shower of sparks and fire, lost forever to the inky void!");
                                        ArrayList<Ship> remainingShips = defender.getTeam().getTeamShips();
                                        remainingShips.remove(defender);
                                        defender.getTeam().setTeamShips(remainingShips);

                                        if (getIsBattleComplete()) {
                                            setIsBattleComplete(true);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        System.out.println("\t\t\tbut the " + weapon.getName() + " hits nothing but vacuum.");
                    }
                }
            }
        }
    }
    public static boolean isAttackDodged(Ship defender, double dodge) {

        double speed = defender.getSpeed();
        double dodgeChance = (speed- defender.getWeight());
        defender.setDodgeChance(dodgeChance);

        if (dodge < Math.min(50.0,dodgeChance)) {

            return true;

        }

        return false;
    }

    public static boolean isCriticalStrike(Random numGenerator) {

        int[] dieVals = roll2d6(numGenerator);

        if (dieVals[0]==dieVals[1]) {

            return true;
        }

        return false;
    }

    public static ShipComponent randomComponentHit(ArrayList<ShipComponent> components, int damageToDefence, Random numGenerator) {
        if (!components.isEmpty()) {
            return components.get(numGenerator.nextInt(0, components.size()));
        }
        return null;
    }

    public static void hitOutput(Ship defender, String whatHit, String damageType, int damageRemaining, boolean isCritical) {

        String whereHit = whatHit;

        if (defender.getComponents().isEmpty()) {

            whereHit = "hull";

        }
        if (isCritical) {
            System.out.println("\t\t\tand OBLITERATES its " + whatHit + " for " + damageRemaining + " damage!");
        }
        else {

            switch (damageType) {

                case ("em"):
                    System.out.println("\t\t\tand zaps its " + whereHit + " for " + damageRemaining + " damage!");
                    break;
                case ("explosive"):
                    System.out.println("\t\t\tand blasts its " + whereHit + " for " + damageRemaining + " damage!");
                    break;
                case ("kinetic"):
                    System.out.println("\t\t\tand pierces its " + whereHit + " for " + damageRemaining + " damage!");
                    break;
                case ("thermal"):
                    System.out.println("\t\t\tand melts its " + whereHit + " for " + damageRemaining + " damage!");
                    break;
                default:
                    System.out.println("\t\t\tand hits its " + whereHit + " for " + damageRemaining + " damage!");
                    break;
            }
        }
    }
    public static String getDamageType(Weapon weapon) {
        return weapon.getDamageType();
    }
    public static int getActualDamage(String damageType, int damageRemaining, Defence protection) {

        double resistance= 0.0;

        switch (damageType) {

            case ("em"):
                resistance = protection.getResistanceToEM();
                break;
            case ("explosive"):
                resistance = protection.getResistanceToExplosive();
                break;
            case ("kinetic"):
                resistance = protection.getResistanceToKinetic();
                break;
            case ("thermal"):
                resistance = protection.getResistanceToThermal();
                break;
            default:
                resistance = 0.0;
                break;

        }

        int actualDamage = (int)(damageRemaining*((100.0-resistance)/100.0));
        return actualDamage;

    }
    public static void hullHit(Ship defender, String damageType, int damageRemaining, boolean isCritical) {

        defender.setBaseIntegrity(defender.getBaseIntegrity()-damageRemaining);

        if (defender.getBaseIntegrity() <= 0) {
            defender.setDestroyed(true);
        }

        hitOutput(defender, "hull", damageType,damageRemaining, isCritical);

    }
    public static ArrayList<Defence> searchForDefences(ArrayList<ShipComponent> components) {

        ArrayList<Defence> defences = new ArrayList<>();

        for (int compNum = 0; compNum < components.size(); compNum++) {

            ShipComponent part = components.get(compNum);

            if (part instanceof Defence) {
                defences.add((Defence) part);
            }

        }
        return defences;
    }
    public static Sensor searchForSensor(ArrayList<ShipComponent> components) {

        for (int compNum = 0; compNum < components.size(); compNum++) {

            ShipComponent part = components.get(compNum);

            if (part instanceof Sensor) {
                if (!part.getisDestroyed()) {
                    return (Sensor) part;
                }
            }

        }
        return null;
    }
}