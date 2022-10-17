package tasks.teleportation.implementations;

import gui.LocationsEnum;
import main.MainScript;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import tasks.teleportation.SpellbookTeleport;

import java.util.HashMap;

public class LumbridgeTeleport extends SpellbookTeleport {


    private long timestampNoTimer;

    public LumbridgeTeleport() {
        super(LocationsEnum.LUMBRIDGE_TELEPORT.getName(), 0,  LocationsEnum.LUMBRIDGE_TELEPORT.getLocation(),  new HashMap<String,Integer>());
    }

    @Override
    public boolean hasTimerActive() {
        return System.currentTimeMillis() < timestampNoTimer;
    }


    @Override
    public void teleport() {
        if (canExecute()) {
            openSpellbook();
            Magic.castSpell(Normal.HOME_TELEPORT);
            MainScript.sleepUntil(() -> Players.localPlayer().isAnimating(), 3000);

            if(!Players.localPlayer().isAnimating()){
                setTimer();
            }else{
                MainScript.sleepUntil(() -> LocationsEnum.LUMBRIDGE_TELEPORT.getLocation().contains(Players.localPlayer()) || !canExecute(), 15000);
            }

            if (!LocationsEnum.LUMBRIDGE_TELEPORT.getLocation().contains(Players.localPlayer())) {
                MainScript.logError("Failed to teleport to lumbridge");
            }
        }
    }


    @Override
    public boolean canExecute() {
        return (!Combat.isPoisoned() || !Players.localPlayer().isInCombat() || !Players.localPlayer().isHealthBarVisible()) && !hasTimerActive() && hasRequiredLevel();
    }

    @Override
    public HashMap<String, Integer> getRequiredItems() {
        return null;
    }



    public void setTimer(){
        timestampNoTimer = System.currentTimeMillis() + 1800000;

    }


}
