package tasks.implementation;

import gui.LocationsEnum;
import logic.WoodcuttingAxeEnum;
import main.MainScript;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.NPC;
import tasks.ExecutableTask;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FishingTask extends ExecutableTask {

    private String[] actions;
    private boolean isCooking = false;
    private boolean executed = false;
    private boolean needItemsForFire;
    private boolean needBank = false;

    public FishingTask(String name, String[] actions, int requiredLevel, HashMap<String, Integer> requiredItem, ArrayList<LocationsEnum> location) {
        super(Skill.FISHING, name, requiredLevel, requiredItem, location);
        this.actions = actions;

    }


    @Override
    public boolean isBankSkill() {
        return false;
    }

    public void execute() {
        if (!executed) {
            failsafes();
        }
        if (!isCooking) {
            if (!Players.localPlayer().isAnimating() && !Inventory.isFull()) {
                MainScript.status = "FishingTask - Fishing";
                fish();
            } else if (Inventory.isFull()) {
                if (true/*!isbanking*/) { //maybe inject core banking handler in every executable task?
                    Inventory.dropAll(e -> !requiredItems.containsKey(e));
                } else {
                    canStart(false);
                    needBank = true;
                    //corehandler goes banking.
                }
            }
        } else {
            if (Inventory.isFull()) {
                if (isCooking && !needItemsForFire) {
                    cookFishExistingFire();
                } else {
                    if (!Inventory.contains(e -> e.getName().contains("logs") || e.getName().contains("Logs"))) {
                        Inventory.drop(e -> e.getName().contains("Raw"));
                    }
                    createFire();
                }
                MainScript.status = "FishingTask - Dropping Items";
                Inventory.dropAll(e -> !requiredItems.containsKey(e.getName()));
            } else {
                MainScript.status = "FishingTask - Idling ... ";
                Mouse.moveMouseOutsideScreen();
                MainScript.sleep(Calculations.random(1000, 12500));
            }
        }
    }


    private void failsafes() {
        if (isCooking) {
            if (getSelectedLocation() != LocationsEnum.BARBARIAN_VILLAGE_FISHING_SPOT) {
                requiredItems.put("Tinderbox", 1);
                requiredItems.put(WoodcuttingAxeEnum.getHighestPossibleAxe(), 1);
                needItemsForFire = true;
            } else {
                needItemsForFire = false;
            }
        }
    }


    @Override
    public List<JComponent> getGuiComponents() {
        JLabel label = new JLabel("Cooking?");
        JCheckBox cooking = new JCheckBox();
        cooking.addActionListener(e -> {
            JCheckBox checkbox = (JCheckBox) e.getSource();
            isCooking = checkbox.isSelected() ? true : false;
        });

        return new ArrayList<>(Arrays.asList(label, cooking));

    }


    private void fish() {
        NPC fishingSpot = NPCs.closest(e -> e.getName().equals("Fishing spot") && e.hasAction(actions[0], actions[1]));
        if (fishingSpot.interact(actions[0])) {
            MainScript.sleepUntil(() -> Players.localPlayer().isAnimating(), 2500);
        }
    }

    private void createFire() {
        //TODO
    }


    private void cookFishExistingFire() {
        //TODO
    }

}

