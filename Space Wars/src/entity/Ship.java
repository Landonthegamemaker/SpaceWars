package entity;

import components.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.XMLUtil;
import util.ShipConstructionUtil;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Ship implements ShipConstructionUtil {

    private String name;
    private int baseIntegrity;
    private double speed;
    private int maxBattery;
    private int currBattery;
    private int batteryRegen;
    private double reaction;
    private double dodgeChance;

    private ArrayList<ShipComponent> components;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaseIntegrity() {
        return baseIntegrity;
    }

    public void setBaseIntegrity(int baseIntegrity) {
        this.baseIntegrity = baseIntegrity;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getMaxBattery() {
        return maxBattery;
    }

    public void setMaxBattery(int maxBattery) {
        this.maxBattery = maxBattery;
    }

    public int getCurrBattery() {
        return currBattery;
    }

    public void setCurrBattery(int currBattery) {
        this.currBattery = currBattery;
    }

    public int getBatteryRegen() {
        return batteryRegen;
    }

    public void setBatteryRegen(int batteryRegen) {
        this.batteryRegen = batteryRegen;
    }

    public double getReaction() {
        return reaction;
    }

    public void setReaction(double reaction) {
        this.reaction = reaction;
    }

    public double getDodgeChance() {
        return dodgeChance;
    }

    public void setDodgeChance(double dodgeChance) {
        this.dodgeChance = dodgeChance;
    }

    public ArrayList<ShipComponent> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<ShipComponent> components) {
        this.components = components;
    }

    public Ship(String name) {
        this.name = name;
        parseXML();
    }

    public void parseXML() {
    Document xmlData = XMLUtil.injestXML();

    int NumAvalibleShips = xmlData.getElementsByTagName("ship").getLength();

    for (int s = 0; s < NumAvalibleShips; s++) {
        Node ship = xmlData.getElementsByTagName("ship").item(s);

        if ((XMLUtil.getChildNodeText(ship, "name")).equals(name)) {
            this.name = XMLUtil.getChildNodeText(ship, "name");
            this.baseIntegrity = convertToInt(XMLUtil.getChildNodeText(ship, "baseIntegrity"));
            this.speed = convertToDouble(XMLUtil.getChildNodeText(ship, "speed"));
            this.maxBattery = convertToInt(XMLUtil.getChildNodeText(ship, "battery"));
            this.currBattery = 0;
            this.batteryRegen = convertToInt(XMLUtil.getChildNodeText(ship, "batteryRegen"));

        ArrayList<ShipComponent> parts = new ArrayList<>();

        int indexOfNextNamedTag = 1;

        Node componentsParentTag = XMLUtil.getChildNode(ship, "components");
        Node nextChildTag = componentsParentTag.getChildNodes().item(indexOfNextNamedTag);
        String tagName = nextChildTag.getNodeName();
        String componentName = XMLUtil.getChildNodeText(componentsParentTag, tagName);

    while (!(tagName.equals("#text"))) {

        if (tagName.equals("laserName")) {

            int laserIndex = 0;

            while (laserIndex <= xmlData.getElementsByTagName("laser").getLength()) {

                if (tagName.equals("laserName")) {

                    Node laserNameTag = xmlData.getElementsByTagName("laser").item(laserIndex);
                    String xmlLaserName = XMLUtil.getChildNodeText(laserNameTag, "name");

                if (componentName.equals(xmlLaserName)) {

                    Laser laserCannon = new Laser(xmlLaserName);

                    laserCannon.setMaxIntegrity(convertToInt(XMLUtil.getChildNodeText(laserNameTag, "integrity")));
                    laserCannon.setCurrIntegrity(laserCannon.getMaxIntegrity());
                    laserCannon.setWeight(convertToDouble(XMLUtil.getChildNodeText(laserNameTag, "weight")));
                    laserCannon.setFireRate(convertToInt(XMLUtil.getChildNodeText(laserNameTag, "rate")));
                    laserCannon.setAccuracy(convertToDouble(XMLUtil.getChildNodeText(laserNameTag, "accuracy")));

                    Node damage = XMLUtil.getChildNode(laserNameTag, "damage");

                    laserCannon.setDamageType(XMLUtil.getChildNodeText(damage, "type"));
                    laserCannon.setDamage(convertToInt(XMLUtil.getChildNodeText(damage, "amount")));
                    laserCannon.setBatteryCost(convertToInt(XMLUtil.getChildNodeText(laserNameTag, "batteryCost")));

                    parts.add(laserCannon);

                    indexOfNextNamedTag += 2;
                    nextChildTag = componentsParentTag.getChildNodes().item(indexOfNextNamedTag);
                    tagName = nextChildTag.getNodeName();
                    componentName = nextChildTag.getTextContent();
                    laserIndex = 0;

                }
                else
                {
                    laserIndex++;
                }

            }
            else
            {
                break;
            }
            }
        }
        if (tagName.equals("railgunName")) {

            int railgunIndex = 0;

            while (railgunIndex < xmlData.getElementsByTagName("railgun").getLength()) {

                if (tagName.equals("railgunName")) {
                    Node railgunTag = xmlData.getElementsByTagName("railgun").item(railgunIndex);
                    String xmlRailgunName = XMLUtil.getChildNodeText(railgunTag, "name");

                if (componentName.equals(xmlRailgunName)) {

                    Railgun turret = new Railgun(xmlRailgunName);

                    turret.setName(XMLUtil.getChildNodeText(railgunTag, "name"));
                    turret.setMaxIntegrity(convertToInt(XMLUtil.getChildNodeText(railgunTag, "integrity")));
                    turret.setCurrIntegrity(turret.getMaxIntegrity());
                    turret.setWeight(convertToDouble(XMLUtil.getChildNodeText(railgunTag, "weight")));
                    turret.setFireRate(convertToInt(XMLUtil.getChildNodeText(railgunTag, "rate")));
                    turret.setAccuracy(convertToDouble(XMLUtil.getChildNodeText(railgunTag, "accuracy")));

                    Node damage = XMLUtil.getChildNode(railgunTag, "damage");

                    turret.setDamageType(XMLUtil.getChildNodeText(damage, "type"));
                    turret.setDamage(convertToInt(XMLUtil.getChildNodeText(damage, "amount")));
                    turret.setAmmo(convertToInt(XMLUtil.getChildNodeText(railgunTag, "ammo")));

                    parts.add(turret);

                    indexOfNextNamedTag += 2;
                    nextChildTag = componentsParentTag.getChildNodes().item(indexOfNextNamedTag);
                    tagName = nextChildTag.getNodeName();
                    componentName = nextChildTag.getTextContent();
                    railgunIndex = 0;

                }
                else
                {
                    railgunIndex++;
                }

            }
            else
            {
                break;
            }
            }
        }
        if (tagName.equals("shieldName")) {

            int shieldIndex = 0;

            while (shieldIndex < xmlData.getElementsByTagName("shield").getLength()) {

                if (tagName.equals("shieldName")) {

                    Node shieldTag = xmlData.getElementsByTagName("shield").item(shieldIndex);
                    String xmlShieldName = XMLUtil.getChildNodeText(shieldTag, "name");

                if (componentName.equals(xmlShieldName)) {

                    Shield shield = new Shield(xmlShieldName);

                    shield.setMaxIntegrity(convertToInt(XMLUtil.getChildNodeText(shieldTag, "integrity")));
                    shield.setCurrIntegrity(0);
                    shield.setWeight(convertToDouble(XMLUtil.getChildNodeText(shieldTag, "weight")));
                    shield.setRegenRate(convertToInt(XMLUtil.getChildNodeText(shieldTag, "regen")));

                    Node resist = XMLUtil.getChildNode(shieldTag, "resistances");

                    shield.setResistanceToEM(convertToDouble(XMLUtil.getChildNodeText(resist, "em")));
                    shield.setResistanceToThermal(convertToDouble(XMLUtil.getChildNodeText(resist, "thermal")));
                    shield.setResistanceToKinetic(convertToDouble(XMLUtil.getChildNodeText(resist, "kinetic")));
                    shield.setResistanceToExplosive(convertToDouble(XMLUtil.getChildNodeText(resist, "explosive")));

                    parts.add(shield);

                    indexOfNextNamedTag += 2;
                    nextChildTag = componentsParentTag.getChildNodes().item(indexOfNextNamedTag);
                    tagName = nextChildTag.getNodeName();
                    componentName = nextChildTag.getTextContent();
                    shieldIndex = 0;

                }
                else
                {
                    shieldIndex++;
                }
            }
            else
            {
                break;
            }
            }
        }
        if (tagName.equals("armorName")) {

            int armorIndex = 0;

            while (armorIndex < xmlData.getElementsByTagName("shield").getLength()) {

                if (tagName.equals("armorName")) {

                    Node armorTag = xmlData.getElementsByTagName("armor").item(armorIndex);
                    String xmlArmorName = XMLUtil.getChildNodeText(armorTag, "name");

                if (componentName.equals(xmlArmorName)) {

                    Armor plating = new Armor(xmlArmorName);

                    plating.setMaxIntegrity(convertToInt(XMLUtil.getChildNodeText(armorTag, "integrity")));
                    plating.setCurrIntegrity(plating.getMaxIntegrity());
                    plating.setWeight(convertToDouble(XMLUtil.getChildNodeText(armorTag, "weight")));

                    Node resist = XMLUtil.getChildNode(armorTag, "resistances");
                    plating.setResistanceToEM(convertToDouble(XMLUtil.getChildNodeText(resist, "em")));
                    plating.setResistanceToThermal(convertToDouble(XMLUtil.getChildNodeText(resist, "thermal")));
                    plating.setResistanceToKinetic(convertToDouble(XMLUtil.getChildNodeText(resist, "kinetic")));
                    plating.setResistanceToExplosive(convertToDouble(XMLUtil.getChildNodeText(resist, "explosive")));

                    parts.add(plating);

                    indexOfNextNamedTag += 2;
                    nextChildTag = componentsParentTag.getChildNodes().item(indexOfNextNamedTag);
                    tagName = nextChildTag.getNodeName();
                    componentName = nextChildTag.getTextContent();
                    armorIndex = 0;

                }
                else
                {
                    armorIndex++;
                }
            }
            else
            {
                break;
            }
            }
        }
        if (tagName.equals("sensorName")) {

            int sensorIndex = 0;

            while (sensorIndex < xmlData.getElementsByTagName("sensor").getLength()) {

                if (tagName.equals("#text")) {

                    this.setComponents(parts);
                    break;
                }

                if (tagName.equals("sensorName")) {

                    Node sensorTag = xmlData.getElementsByTagName("sensor").item(sensorIndex);
                    String xmlSensorName = XMLUtil.getChildNodeText(sensorTag, "name");

                if (componentName.equals(xmlSensorName)) {

                    Sensor sensor = new Sensor(componentName);

                    sensor.setMaxIntegrity(convertToInt(XMLUtil.getChildNodeText(sensorTag, "integrity")));
                    sensor.setCurrIntegrity(sensor.getMaxIntegrity());
                    sensor.setWeight(convertToDouble(XMLUtil.getChildNodeText(sensorTag, "weight")));
                    sensor.setAccuracyBoost(convertToDouble(XMLUtil.getChildNodeText(sensorTag, "accuracy")));

                    parts.add(sensor);

                    indexOfNextNamedTag += 2;
                    nextChildTag = componentsParentTag.getChildNodes().item(indexOfNextNamedTag);

                    if (nextChildTag == null) {
                        tagName = "#text";
                        this.setComponents(parts);
                        break;
                    }

                    indexOfNextNamedTag += 2;
                    tagName = nextChildTag.getNodeName();
                    componentName = nextChildTag.getTextContent();
                    sensorIndex = 0;

                }
                else
                {
                    sensorIndex++;
                }
            }
            else
            {
                break;
            }
            }
        }
    }
    }
    }
    }

    @Override
    public int convertToInt (String data){
        return Integer.parseInt(data);
    }

    @Override
    public double convertToDouble (String data){
        return Double.parseDouble(data);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
