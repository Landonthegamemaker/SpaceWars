package util;
import components.*;
import entity.Ship;
import entity.Team;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public abstract class BattleUtil {

    public static boolean isBattleComplete;
    private static final int MAX_NUMBER_OF_TURNS = 20;

    public static boolean isIsBattleComplete() {
        return isBattleComplete;
    }

    public static void setIsBattleComplete(boolean isBattleComplete) {
        BattleUtil.isBattleComplete = isBattleComplete;
    }

    public static int getMAX_NUMBER_OF_TURNS() {
        return MAX_NUMBER_OF_TURNS;
    }

    public static void battle(Team team1, Team team2, Random numGenerator) {

        setIsBattleComplete(false);

        while (!isBattleComplete) {

            for (int turnNum = 0; turnNum <= getMAX_NUMBER_OF_TURNS(); turnNum++) {

                if (!(team1.getTeamShips().isEmpty()) && !(team2.getTeamShips().isEmpty())) {

                    ArrayList<Ship> team1TurnOrder = generateShipReaction(team1.getTeamShips(), numGenerator);
                    team1.setTeamShips(team1TurnOrder);
                    ArrayList<Ship> team2TurnOrder = generateShipReaction(team2.getTeamShips(), numGenerator);
                    team2.setTeamShips(team2TurnOrder);

                    System.out.println("Turn " + turnNum + " has begun!");

                    teamTurn(team1, team1TurnOrder, team2, numGenerator);
                    teamTurn(team2, team2TurnOrder, team1, numGenerator);
                    System.out.println(battleOutcome(team1, team2));

                }
            }
            System.out.println("Exhausted from battle, all ships flee seeking repairs.");
        }
    }

    public static ArrayList<Ship> generateShipReaction(ArrayList<Ship> teamShips, Random numGenerator) {

        //Uses battle.Random class to generate ship reaction between 0 and its speed (exclusive)

        Ship ship;
        double maxReact;
        double reaction;
        ArrayList<Double> totalReactions = new ArrayList<>();

        for (int i = 0; i < teamShips.size(); i++) {

            ship = teamShips.get(i);

            if (ship.getBaseIntegrity() > 0) {

                maxReact = ship.getSpeed();
                DoubleStream doublesGenerated = numGenerator.doubles(1, 0, maxReact);
                reaction = doublesGenerated.sum();
                ship.setReaction(reaction);

                if (!(totalReactions.contains(reaction))) {

                    totalReactions.add(reaction);

                }

                else {

                    doublesGenerated = numGenerator.doubles(1, 0, maxReact);
                    reaction = doublesGenerated.sum();
                    ship.setReaction(reaction);

                }
            }
        }

        ArrayList<Ship> turnOrder = new ArrayList<>();
        Ship ship1 = teamShips.get(0);
        maxReact = ship1.getReaction();
        Ship nextShip;

        for (int i = 0; i < totalReactions.size(); i++) {

            for (int j = i+1; j < totalReactions.size(); j++) {
                nextShip = teamShips.get(i);

                if (nextShip.getReaction() > maxReact) {
                    maxReact = nextShip.getReaction();
                    ship1 = nextShip;
                }
            }
            turnOrder.add(ship1);
        }

        return turnOrder;
    }

    public static void teamTurn(Team attackingTeam, ArrayList<Ship> attackingTeamShips, Team defendingTeam, Random numGenerator) {

        for (int shipsActed = 0; shipsActed < attackingTeamShips.size(); shipsActed++) {

            Ship attacker = attackingTeam.getTeamShips().get(shipsActed);
            ArrayList<Weapon> blastersReady = beginTurn(attackingTeam, attacker);

            Ship target = targetShip(defendingTeam.getTeamShips());

            if (target == null) {

                System.out.println(battleOutcome(attackingTeam,defendingTeam));
                break;

            }
            else {

                fireAllWeapons(attackingTeam, attacker, blastersReady, defendingTeam, target, numGenerator);

            }

        }

    }

    public static ArrayList<Weapon> beginTurn(Team team, Ship ship) {

        System.out.println("\t" + team.getTeamName() + "'s " + ship.getName() + " ominously maneuvers into firing position.");

        ArrayList<ShipComponent> shipComponents = ship.getComponents();

        if (searchForDefence(shipComponents) != null) {

            if (searchForDefence(shipComponents) instanceof Shield) {

                Shield shield = (Shield) searchForDefence(shipComponents);

                while (shield.getCurrIntegrity() < shield.getMaxIntegrity()) {

                    if (shield.getCurrIntegrity() >= ship.getMaxBattery()) {
                        shield.setCurrIntegrity(shield.getMaxIntegrity());
                    }

                    int increaseInt = (int) (shield.getCurrIntegrity() + shield.getRegenRate());
                    shield.setCurrIntegrity(increaseInt);

                }
            }
        }


        while (ship.getCurrBattery() < ship.getMaxBattery()) {

            int rechargeRate = ship.getBatteryRegen();
            int remainingbattery = ship.getMaxBattery() - ship.getCurrBattery();

            ship.setCurrBattery(ship.getCurrBattery() + rechargeRate);

            if (rechargeRate > remainingbattery) {
                ship.setCurrBattery(ship.getMaxBattery());
                break;
            }

        }

        ArrayList<Weapon> weapons = new ArrayList<>();
        ArrayList<ShipComponent> comps = ship.getComponents();
        ShipComponent component;

        for (int i = 0; i < comps.size(); i++) {
            component = comps.get(i);

            if (component instanceof Weapon) {

                if (!(component.getName().contains("DESTROYED"))) {

                    weapons.add((Weapon) component);

                }
            }
        }
        return weapons;
    }

    public static Ship targetShip(ArrayList<Ship> opponentShips) {

        for (int i = 0; i < opponentShips.size(); i++) {

            Ship target = opponentShips.get(i);

            if (!(target.getName().contains("DESTROYED"))) {

                return target;

            }

        }
        return null;

    }
    public static double roll(Random numGenerator) {

        //Generates a random number between 0 and 100 (exclusive)

        DoubleStream oneToHundred = numGenerator.doubles(1,0,100);
        double percentCalculated = oneToHundred.sum();

        return percentCalculated;
    }

    public static int[] roll2d6(Random numGenerator) {

        //Rolls 2 6-sided die to generate 2 random numbers between 1 and 6 (inclusive)

        IntStream roll6d1 = numGenerator.ints(1,1,7);
        IntStream roll6d2 = numGenerator.ints(1,1,7);

        int die1 = roll6d1.sum();
        int die2 = roll6d2.sum();

        int[] dieVals = {die1,die2};

        return dieVals;
    }

    public static void fireAllWeapons(Team attackingTeam, Ship attacker, ArrayList<Weapon> weapons, Team defendingTeam, Ship shipBeingAttacked, Random numGenerator) {

        for (int weaponAtIndex = 0; weaponAtIndex < weapons.size(); weaponAtIndex++) {

            Weapon weapon = weapons.get(weaponAtIndex);
            ArrayList<ShipComponent> components = attacker.getComponents();
            int initalDamage = weapon.getDamage();
            int damageRemaining = initalDamage;
            boolean isCrit;
            double accuracy = weapon.getAccuracy();
            double accuracyBoost = 0.0;
            ShipComponent partHit;

            for (int fired = 0; fired < weapon.getFireRate(); fired++) {

                if (weapon instanceof Laser) {

                    int cost = ((Laser) weapon).getBatteryCost();

                    if (cost <= attacker.getCurrBattery() && weapon.getCurrIntegrity() > 0) {

                        int drainBattery = (attacker.getCurrBattery()-cost);
                        attacker.setCurrBattery(drainBattery);

                    }
                }

                if (weapon instanceof Railgun) {

                    int ammo = ((Railgun) weapon).getAmmo();

                    if (ammo > 0) {

                        ammo--;

                        ((Railgun) weapon).setAmmo(ammo);

                    }
                }

                boolean react = isAttackDodged(shipBeingAttacked,numGenerator);

                if (react) {

                    System.out.println("\t\t\tbut the " + defendingTeam.getTeamName() + "'s " + shipBeingAttacked.getName() + " deftly avoids the blow.");

                }

                Sensor sensor = searchForSensor(components);

                if (sensor != null) {

                    accuracyBoost = sensor.getAccuracyBoost();

                }

                System.out.println("\t\t" + attackingTeam.getTeamName() + "'s " + attacker.getName() + " fires its " + weapons.get(weaponAtIndex).getName() + " at " + defendingTeam.getTeamName() + "'s " + shipBeingAttacked.getName() + "...");

                double oneToHundred = roll(numGenerator);

                if (oneToHundred < (accuracy + accuracyBoost)) {

                    isCrit = isCriticalStrike(numGenerator);

                    if (isCrit) {

                        initalDamage = 2 * weapon.getDamage();

                        if (components.isEmpty()) {

                            hullHit(shipBeingAttacked,getDamageType(weapon),initalDamage, isCrit);

                            if (isShipDestroyed(defendingTeam,shipBeingAttacked)) {
                                ArrayList<Ship> remainingShips = removeDestroyedShips(defendingTeam.getTeamShips(),shipBeingAttacked);
                                defendingTeam.setTeamShips(remainingShips);
                            };

                        }

                        else {

                            partHit = randomComponentHit(components, initalDamage, numGenerator);
                            partHit.setCurrIntegrity(partHit.getCurrIntegrity() - initalDamage);
                            hitOutput(shipBeingAttacked, partHit.getName(),getDamageType(weapon),initalDamage,isCrit);

                            if (isComponentDestroyed(components,partHit,initalDamage)) {

                                ArrayList<ShipComponent> remainingComps = removeDestroyedComponents(components,partHit);
                                shipBeingAttacked.setComponents(remainingComps);

                            }


                        }


                    }

                    else {

                        while (damageRemaining > 0) {

                            boolean check1 = false;

                            boolean check2 = false;

                            if (searchForDefence(components) != null) {

                                if (searchForDefence(components) instanceof Shield) {

                                    Shield shield = (Shield) searchForDefence(components);

                                    int actualDamage = getActualDamage(getDamageType(weapon), initalDamage, shield);

                                    if (actualDamage > shield.getCurrIntegrity()) {

                                        damageRemaining = actualDamage - shield.getCurrIntegrity();
                                        initalDamage = actualDamage - damageRemaining;
                                        shield.setCurrIntegrity(0);

                                        check1 = isShieldDown(shield);
                                        hitOutput(shipBeingAttacked, shield.getName(), getDamageType(weapon), initalDamage, isCrit);

                                    } else {

                                        damageRemaining = 0;
                                        shield.setCurrIntegrity(shield.getCurrIntegrity() - actualDamage);
                                        hitOutput(shipBeingAttacked, shield.getName(), getDamageType(weapon), actualDamage,isCrit);

                                    }
                                }
                                else if (searchForDefence(components) instanceof Armor) {

                                    check2 = false;

                                    Armor armor = (Armor) shipBeingAttacked.getComponents().get(shipBeingAttacked.getComponents().indexOf("Armor"));

                                    int actualDamage = getActualDamage(getDamageType(weapon), damageRemaining, armor);

                                    if (actualDamage > armor.getCurrIntegrity()) {

                                        damageRemaining = actualDamage - armor.getCurrIntegrity();
                                        initalDamage = actualDamage - damageRemaining;
                                        armor.setCurrIntegrity(0);

                                        check2 = isComponentDestroyed(components, armor, damageRemaining);
                                        hitOutput(shipBeingAttacked, armor.getName(), getDamageType(weapon), initalDamage, isCrit);

                                    } else {

                                        damageRemaining = 0;
                                        armor.setCurrIntegrity(armor.getCurrIntegrity() - actualDamage);
                                        hitOutput(shipBeingAttacked, armor.getName(), getDamageType(weapon), actualDamage,isCrit);
                                    }

                                }

                            }
                            else if (check1 && check2) {

                                String damageType = getDamageType(weapon);
                                hullHit(shipBeingAttacked, damageType, damageRemaining, isCrit);

                                if (isShipDestroyed(defendingTeam, shipBeingAttacked)) {

                                    ArrayList updatedShips = removeDestroyedShips(defendingTeam.getTeamShips(), shipBeingAttacked);
                                    defendingTeam.setTeamShips(updatedShips);

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
    public static boolean isAttackDodged(Ship shipBeingAttacked, Random numGenerator) {

        double speed = shipBeingAttacked.getSpeed();
        double componentWeights = 0;

        for (int i = 0; i < shipBeingAttacked.getComponents().size();i++) {

            componentWeights += shipBeingAttacked.getComponents().get(i).getWeight();

        }
        double dodgeChance = (speed - componentWeights);

        if (dodgeChance > 50) {

        dodgeChance = 50;

        }

        shipBeingAttacked.setDodgeChance(dodgeChance);
        double dodgePercent = roll(numGenerator);

        if (dodgePercent < dodgeChance) {

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

    public static ShipComponent randomComponentHit(ArrayList<ShipComponent> components, int initalDamage, Random numGenerator) {

        IntStream randomIndex = numGenerator.ints(1,0,components.size());
        int componentIndex = randomIndex.sum();

        ShipComponent componentDamaged = components.get(componentIndex);

        return componentDamaged;
    }

    public static void hitOutput(Ship shipBeingAttacked, String whatHit, String damageType, int damageRemaining, boolean isCritical) {

        String whereHit = whatHit;

        if (shipBeingAttacked.getComponents().isEmpty()) {

            whereHit = "hull";

        }
        if (isCritical) {

            System.out.println("\t\t\t and OBLITERATES its " + whatHit + " for " + damageRemaining + " damage!");

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
                    System.out.println("\t\t\tand pierces its " + whereHit + "  for " + damageRemaining + " damage!");
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

    public static boolean isComponentDestroyed(ArrayList<ShipComponent> shipComponents, ShipComponent partHit, int initalDamage) {

        if (partHit.getCurrIntegrity() <= 0) {

            System.out.println("The " + partHit.getName() + " is destroyed.");
            partHit.setName("DESTROYED " + partHit.getName());

        }

        return false;
    }

    public static ArrayList<ShipComponent> removeDestroyedComponents(ArrayList<ShipComponent> components, ShipComponent component) {

        if (component.getName().contains("DESTROYED")) {

            components.remove(component);

        }
        return components;
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

    public static boolean isShieldDown(Shield shield) {
        // Ensure shield integrity never falls below 0

        if (shield.getCurrIntegrity() <= 0) {

            shield.setCurrIntegrity(0);
            System.out.println("\t\t\tThe " + shield.getName() + " dissipates.");

            return true;

        }

        return false;

    }

    public static void hullHit(Ship shipBeingAttacked, String damageType, int damageRemaining, boolean isCritical) {

        shipBeingAttacked.setBaseIntegrity(shipBeingAttacked.getBaseIntegrity()-damageRemaining);
        hitOutput(shipBeingAttacked, "hull", damageType,damageRemaining, isCritical);

    }

    public static boolean isShipDestroyed(Team team, Ship ship) {
    // Check Ship integrity, if below 0, it is true

        if (ship.getBaseIntegrity() <= 0) {

            System.out.println("\t" + team.getTeamName() + "’s " + ship.getName() + " EXPLODES in a shower of sparks and fire, lost forever to the inky void!");
            ship.setName("DESTROYED " + ship.getName());


            return true;
        }

        return false;

    }

    public static ArrayList<Ship> removeDestroyedShips(ArrayList<Ship> teamShips, Ship removing) {

        if (removing.getName().contains("DESTROYED")) {

            teamShips.remove(removing);

        }

        return teamShips;
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

        public static Defence searchForDefence(ArrayList<ShipComponent> components) {

            for (int compNum = 0; compNum < components.size(); compNum++) {

                ShipComponent part = components.get(compNum);

                if (part instanceof Defence) {

                    return (Defence) part;

                }

            }

            return null;
        }

    public static String battleOutcome(Team team1, Team team2) {

        if (team1.getTeamShips().isEmpty()) {
            setIsBattleComplete(true);
            System.out.println(team2.getTeamName() + " has won the battle. " + team1.getTeamName() + " suffers ignominious defeat.");

        }

        if (team2.getTeamShips().isEmpty()) {
            setIsBattleComplete(true);
            return team1.getTeamName() + " has won the battle. " + team2.getTeamName() + " suffers ignominious defeat.";
        }
        return "";
    }
}