package tasks.implementation;

import gui.LocationsEnum;
import logic.WoodcuttingAxeEnum;
import main.MainScript;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import tasks.ExecutableTask;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WoodcuttingTask extends ExecutableTask {

    private boolean isFiremaking = false;
    private boolean isLightingFire = false;
    private String logs;
    private String axe;
    private boolean executed = false;


    public WoodcuttingTask(String name, String logs, int minimumLevel, HashMap<String, Integer> requiredItems, ArrayList<LocationsEnum> location) {
        super(Skill.WOODCUTTING, name, minimumLevel, requiredItems, location);
        this.logs = logs;
        this.axe = WoodcuttingAxeEnum.getHighestPossibleAxe();
        requiredItems.put(WoodcuttingAxeEnum.getHighestPossibleAxe(),1);
    }

    @Override
    public boolean isBankSkill() {
        return false;
    }

    @Override
    public void execute() {
        if(!executed){
            failsafe();
        }
        if (Inventory.isFull()) {
            if (isFiremaking) {
                if (isOnFiremakingSpot()) {
                    MainScript.status = "WoodcuttingTask - Lighting fire.";
                    lightFire(); //while logs in inventory  || ( logs < Calculations.random(1,5) && !onValidSpot() )
                } else {
                    MainScript.status = "WoodcuttingTask - Walking to valid location to light fire.";
                    goToFiremakingSpot();
                }
            } else {
                MainScript.status = "WoodcuttingTask - Dropping all items";
                Inventory.dropAll(e -> !requiredItems.containsKey(e.getName()));
            }
        } else {
            if (Players.localPlayer().isAnimating()) {
                MainScript.status = "WoodcuttingTask - Idling ...";
                MainScript.sleep(Calculations.random(1000, 12500));
            } else {
                MainScript.status = "WoodcuttingTask - Chopping tree";
                chopTree();
            }
        }
    }

    private void failsafe() {
        if (isFiremaking) {
            requiredItems.put("Tinderbox", 1);
        }

        while(Inventory.contains(axe) && WoodcuttingAxeEnum.canEquip(axe)){
            if(Equipment.equip(EquipmentSlot.WEAPON, axe)){
                MainScript.sleepUntil(() -> Equipment.getItemInSlot(EquipmentSlot.WEAPON).getName() == axe, 1500);
            }
        }
        executed = true;
    }


    @Override
    public List<JComponent> getGuiComponents() {
        JLabel label = new JLabel("Firemaking?");
        JCheckBox cooking = new JCheckBox();
        cooking.addActionListener(e -> {
            JCheckBox checkbox = (JCheckBox) e.getSource();
            isFiremaking = checkbox.isSelected() ? true : false;
        });

        return new ArrayList<>(Arrays.asList(label, cooking));

    }

    private boolean goToFiremakingSpot() {
        //TODO
        //returns true if on spot
        return false;
    }

    private boolean isOnFiremakingSpot() {
        //TODO
        return false;
    }
    private boolean isOnValidSpot() {
        //TODO
        return false;
    }

    private void chopTree() {
        //TODO
    }


    private void lightFire() {
        // ....

        while ((Inventory.contains(logs) && isOnValidSpot()) || (Inventory.count(logs) < Calculations.random(1, 5) && !isOnValidSpot()))
            MainScript.logInfo("Finished lighting logs, will go back to chopping trees");
    }

}
