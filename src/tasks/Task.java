package tasks;

import gui.LocationsEnum;
import org.dreambot.api.methods.skills.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Task {

    protected Skill skill;
    protected int minimumLevel;
    protected HashMap<String, Integer> requiredItems;
    protected String method;
    protected List<LocationsEnum> locations;

public Task(Skill skill, String method, int minimumLevel, HashMap<String, Integer> requiredItems, ArrayList<LocationsEnum> locations){
        this.skill = skill;
        this.method = method;
        this.minimumLevel = minimumLevel;
        this.requiredItems = requiredItems;
        this.locations = locations;

    }

    @Override
    public String toString(){
        return String.format("%s,%s,%d", skill.getName(), method, minimumLevel);
    }


}
