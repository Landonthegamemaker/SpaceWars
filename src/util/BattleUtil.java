package util;
import components.*;
import entity.Ship;
import entity.Team;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

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

                if (!team1.getTeamShips().isEmpty() || !team2.getTeamShips().isEmpty()) {
                    ArrayList<Ship> team1TurnOrder = generateShipReactions(team1.getTeamShips(), numGenerator);
                    team1.setTeamShips(team1TurnOrder);
                    ArrayList<Ship> team2TurnOrder = generateShipReactions(team2.getTeamShips(), numGenerator);
                    team2.setTeamShips(team2TurnOrder);

                    teamTurn(team1, team1TurnOrder, team2, numGenerator);
                    teamTurn(team2, team2TurnOrder, team1, numGenerator);
                    turnNum++;
                }
                else {
                    setIsBattleComplete(true);
                    break;
                }
        }
    }

    public static ArrayList<Ship> generateShipReactions(ArrayList<Ship> teamShips, Random numGenerator) {

        //Uses Random class to generate ship reaction between 0 and its speed (exclusive)

        double reaction = 0.0;
        ArrayList<Double> uniqueReactions = new ArrayList<>();
        HashMap<Double,Ship> hash = new HashMap<>();

        for (Ship ship: teamShips) {
            do {
                if (!ship.isDestroyed()) {
                    reaction = numGenerator.nextDouble(0,ship.getSpeed());
                }
            } while (uniqueReactions.contains(reaction)); {
                ship.setReaction(reaction);
                uniqueReactions.add(reaction);
                hash.put(reaction,ship);
            }
        }

        //For Each loop on keyset, Add each element to an ArrayList

        Collections.sort(uniqueReactions,Comparator.comparing(Double::floatValue).reversed());

        ArrayList<Ship> turnOrder = new ArrayList<>();

        //For Each in that ArrrayList, add to another ArrayList of ships
        //HashMap.get(key) from elements of first ArrrayList

        for (Double key: uniqueReactions) {
            turnOrder.add(hash.get(key));
        }

        return turnOrder;
    }

    public static void teamTurn(Team attackingTeam, ArrayList<Ship> attackingTeamShips, Team defendingTeam, Random numGenerator) {

        for (int actingShip = 0; actingShip < attackingTeamShips.size(); actingShip++) {

            Ship attacker = attackingTeam.getTeamShips().get(actingShip);
            ArrayList<Weapon> blastersReady = beginTurn(attackingTeam, attacker);

            System.out.println("\t" + attackingTeam.getTeamName() + "’s " + attackingTeamShips.get(actingShip).getName() + " ominously maneuvers into firing position.");

            Ship target = targetShip(defendingTeam.getTeamShips(), numGenerator);

            if (target == null) {

                setIsBattleComplete(true);
                break;

            }
            else {

                fireAllWeapons(attackingTeam, attacker, blastersReady, defendingTeam, target, numGenerator);

            }

        }
    }

    public static ArrayList<Weapon> beginTurn(Team team, Ship ship) {

        ArrayList<Defence> shipComponents = searchForDefences(ship.getComponents());

        //Replenishing Shield
        if (!shipComponents.isEmpty()) {
            for (Defence component: shipComponents) {
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

        //Readying Weapons

        ArrayList<Weapon> weapons = new ArrayList<>();
        ArrayList<ShipComponent> comps = ship.getComponents();
        ShipComponent component;

        for (int i = 0; i < comps.size(); i++) {
            component = comps.get(i);

            if (component instanceof Weapon && !component.isComponentDestroyed()) {

                weapons.add((Weapon) component);

            }
        }
        return weapons;
    }

    public static Ship targetShip(ArrayList<Ship> opponentShips, Random numGenerator) {

        while (!opponentShips.isEmpty()) {
            Ship target = opponentShips.get(numGenerator.nextInt(0, opponentShips.size()));
            if (!target.isDestroyed()) {
                return target;
            }
        }
        return null;
    }
    public static double rollOneToHundred(Random numGenerator) {
        //Generates a random floating-point number between 0 and 100 (exclusive)

        double percentCalculated = numGenerator.nextDouble(0,100);
//        System.out.print("Random: " + percentCalculated);
        return percentCalculated;
    }

    public static int[] roll2d6(Random numGenerator) {
        //Rolls 2 6-sided die to generate 2 random numbers between 1 and 6 (inclusive)

        int die1 = numGenerator.nextInt(1,7);
        int die2 = numGenerator.nextInt(1,7);

        int[] dieVals = {die1,die2};

        return dieVals;
    }

    public static void fireAllWeapons(Team attackingTeam, Ship attacker, ArrayList<Weapon> attackerWeapons, Team defendingTeam, Ship shipBeingAttacked, Random numGenerator) {

            if (!getIsBattleComplete()) {

                ArrayList<ShipComponent> attackerComponents = attacker.getComponents();
                ArrayList<ShipComponent> defenderComponents = shipBeingAttacked.getComponents();
                attacker.setWeight(attackerComponents);
                shipBeingAttacked.setWeight(defenderComponents);
                ArrayList<Defence> defences = searchForDefences(defenderComponents);

                for (int weaponAtIndex = 0; weaponAtIndex < attackerWeapons.size(); weaponAtIndex++) {
                    Weapon weapon = attackerWeapons.get(weaponAtIndex);
                    double accuracy = weapon.getAccuracy();

                for (int fired = 0; fired < weapon.getFireRate(); fired++) {

                    int damageRemaining = weapon.getDamage();
                    double accuracyBoost = 0.0;

                    Sensor sensor = searchForSensor(attackerComponents);

                    if (sensor != null && !sensor.isComponentDestroyed()) {
                        accuracyBoost = sensor.getAccuracyBoost();

                    }

                    if (weapon instanceof Laser) {

                        int cost = ((Laser) weapon).getBatteryCost();

                        if (cost <= attacker.getCurrBattery() && !weapon.isComponentDestroyed()) {

                            int drainBattery = (attacker.getCurrBattery() - cost);
                            attacker.setCurrBattery(drainBattery);

                        } else {

                            break;

                        }
                    }
                    else if (weapon instanceof Railgun) {

                        int ammo = ((Railgun) weapon).getAmmo();

                        if (ammo > 0 && !weapon.isComponentDestroyed()) {

                            ammo--;
                            ((Railgun) weapon).setAmmo(ammo);

                        } else {

                            break;

                        }
                    }
                    System.out.println("\t\t" + attackingTeam.getTeamName() + "’s " + attacker.getName() + " fires its " + attackerWeapons.get(weaponAtIndex).getName() + " at " + defendingTeam.getTeamName() + "’s " + shipBeingAttacked.getName() + " …");


                    if (isAttackDodged(shipBeingAttacked, rollOneToHundred(numGenerator))) {
                        System.out.println("\t\t\tbut the " + defendingTeam.getTeamName() + "’s " + shipBeingAttacked.getName() + " deftly avoids the blow.");
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
                            if (randomComponent == null) {
                                String damageType = getDamageType(weapon);
                                hullHit(shipBeingAttacked, damageType, damageRemaining, true);
                            }
                            randomComponent.setCurrIntegrity(randomComponent.getCurrIntegrity() - damageRemaining);
                            hitOutput(shipBeingAttacked, randomComponent.getName(), getDamageType(weapon), damageRemaining, true);

                            if (randomComponent.isComponentDestroyed()) {

                                if (randomComponent instanceof Shield) {
                                    System.out.println("\t\t\tThe " + randomComponent.getName() + " dissipates.");
                                }
                                else {
                                    System.out.println("\t\t\tThe " + randomComponent.getName() + " is destroyed.");
                                    shipBeingAttacked.getComponents();
                                }
                                if (shipBeingAttacked.isDestroyed()) {

                                    System.out.println("\t" + defendingTeam.getTeamName() + "’s " + shipBeingAttacked.getName() + " EXPLODES in a shower of sparks and fire, lost forever to the inky void!");
                                    ArrayList<Ship> remainingShips = defendingTeam.getTeamShips();
                                    remainingShips.remove(shipBeingAttacked);
                                    defendingTeam.setTeamShips(remainingShips);

                                    if (defendingTeam.getTeamShips().isEmpty()) {
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
                                        hitOutput(shipBeingAttacked, shield.getName(), getDamageType(weapon), actualDamage, false);
                                    }
                                }
                                if (damageRemaining > 0) {

                                    if (component instanceof Armor) {

                                        armor = (Armor) component;

                                        if (!armor.isComponentDestroyed()) {
                                            int actualDamage = getActualDamage(getDamageType(weapon), damageRemaining, armor);
                                            damageRemaining = actualDamage - armor.getCurrIntegrity();
                                            if (actualDamage >= armor.getCurrIntegrity()) {
                                                actualDamage = armor.getCurrIntegrity();
                                                armor.setCurrIntegrity(0);
                                                System.out.println("\t\t\tThe " + armor.getName() + " is destroyed.");
                                                break;
                                            }
                                            else {
                                                armor.setCurrIntegrity(armor.getCurrIntegrity()-actualDamage);
                                                damageRemaining = 0;
                                            }
                                            hitOutput(shipBeingAttacked, armor.getName(), getDamageType(weapon), actualDamage, false);
                                        }
                                    }
                                }
                           }
                            if (defences.isEmpty() || (shield.getPassThrough() && armor.isComponentDestroyed())) {
                                if (damageRemaining > 0) {

                                    String damageType = getDamageType(weapon);
                                    hullHit(shipBeingAttacked, damageType, damageRemaining, false);

                                    if (shipBeingAttacked.isDestroyed()) {

                                        System.out.println("\t" + defendingTeam.getTeamName() + "’s " + shipBeingAttacked.getName() + " EXPLODES in a shower of sparks and fire, lost forever to the inky void!");
                                        ArrayList<Ship> remainingShips = defendingTeam.getTeamShips();
                                        remainingShips.remove(shipBeingAttacked);
                                        defendingTeam.setTeamShips(remainingShips);

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
    public static boolean isAttackDodged(Ship shipBeingAttacked, double dodge) {

        double speed = shipBeingAttacked.getSpeed();
        double dodgeChance = (speed- shipBeingAttacked.getWeight());
        shipBeingAttacked.setDodgeChance(dodgeChance);

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

    public static void hitOutput(Ship shipBeingAttacked, String whatHit, String damageType, int damageRemaining, boolean isCritical) {

        String whereHit = whatHit;

        if (shipBeingAttacked.getComponents().isEmpty()) {

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
    public static void hullHit(Ship shipBeingAttacked, String damageType, int damageRemaining, boolean isCritical) {

        shipBeingAttacked.setBaseIntegrity(shipBeingAttacked.getBaseIntegrity()-damageRemaining);

        if (shipBeingAttacked.getBaseIntegrity() <= 0) {
            shipBeingAttacked.setDestroyed(true);
        }

        hitOutput(shipBeingAttacked, "hull", damageType,damageRemaining, isCritical);

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
                return (Sensor) part;
            }

        }
        return null;
    }
}