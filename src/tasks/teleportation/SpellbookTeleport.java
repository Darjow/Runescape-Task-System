package tasks.teleportation;

import logic.TeleportationType;
import main.MainScript;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;

import java.util.HashMap;

public abstract class SpellbookTeleport extends TeleportationMethod{


    private HashMap<String, Integer> runesNeeded;
    private int required_level;

    public SpellbookTeleport(String name, int required_level,  Area destination, HashMap<String,Integer> runesNeeded) {
        super(name, destination, TeleportationType.SPELLBOOK);
        this.runesNeeded = runesNeeded;
        this.required_level = required_level;
    }

    public void openSpellbook(){
        while(Tabs.getOpen() != Tab.MAGIC){
            if(Tabs.open(Tab.MAGIC)){
                MainScript.sleepUntil(() -> Tabs.getOpen() == Tab.MAGIC, 500);
            }
        }
    }

    public boolean hasRequiredLevel(){
        return Skills.getRealLevel(Skill.MAGIC) >= required_level;
    }


}
