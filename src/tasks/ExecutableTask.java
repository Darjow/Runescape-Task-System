package tasks;

import gui.LocationsEnum;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import tasks.teleportation.TeleportationMethod;


import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ExecutableTask extends Task{


    private LocationsEnum selectedLocation;
    private int maxLevel;
    private TeleportationMethod teleportMethod;
    private boolean needsBanking;

    private boolean canStart = false;



    public ExecutableTask(Skill skill, String method, int minimumLevel, HashMap<String, Integer> requiredItems, ArrayList<LocationsEnum> location) {
        super(skill, method, minimumLevel, requiredItems, location);
        this.needsBanking = needsBanking;
    }

    public void setSelectedLocation(LocationsEnum location){
        this.selectedLocation = location;
    }
    public LocationsEnum getSelectedLocation(){
        return selectedLocation;
    }
    public String getMethod(){
        return method;
    }
    public List<LocationsEnum> getLocations(){
        return locations;
    }
    public Skill getSkill(){
        return skill;
    }
    public int getMaxLevel(){
        return maxLevel;
    }
    public int getMinLevel(){return minimumLevel;}
    public HashMap<String, Integer> getRequiredItems(){
        return requiredItems;
    }
    public void setMaxLevel(int level){
        this.maxLevel = level;
    }
    public boolean canExecute(){
        return Skills.getRealLevel(skill) >= minimumLevel && Skills.getRealLevel(skill) < maxLevel;
    }
    public void setNeedBankingSupport(boolean b){
        this.needsBanking = b;
    }
    public boolean needsBankingSupport(){
        return needsBanking;
    }
    public abstract boolean isBankSkill();
    public abstract void execute();
    public abstract List<JComponent> getGuiComponents();

    @Override
    public String toString(){
        return String.format("%s,%s,%d,%s,%s", skill.getName(), method, maxLevel, selectedLocation == null?  "null" : selectedLocation.getName(), teleportMethod == null? "null" : teleportMethod.getName());
    }

    public TeleportationMethod getTeleportationMethod() {
        return teleportMethod;
    }
    public boolean canStart(){
        return canStart;
    }
    public void canStart(boolean b) {
        canStart = b;
    }
    public void setTransportationMethod(TeleportationMethod teleportationMethod) {
        this.teleportMethod = teleportationMethod;
    }



}
