package logic;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum WoodcuttingAxeEnum {
    BRONZE_AXE("Bronze axe", 1, 1),
    IRON_AXE("Iron axe", 1, 1),
    STEEL_AXE("Steel axe", 6, 5),
    BLACK_AXE("Black axe", 11,10),
    MITHRIL_AXE("Mithril axe", 21,20),
    ADAMANT_AXE("Adamant axe", 31, 30),
    RUNE_AXE("Rune axe", 41,40),
    DRAGON_AXE("Dragon axe", 61,60),
    ;


    private String name;
    private int woodcutting_level_required;
    private int attack_level_required;


    private WoodcuttingAxeEnum(String name, int woodcutting_level_required, int attack_level_required){
        this.name = name;
        this.woodcutting_level_required = woodcutting_level_required;
        this.attack_level_required = attack_level_required;

    }

    public static String getHighestPossibleAxe(){
        List<WoodcuttingAxeEnum> axesCanUse = Arrays.stream(values()).filter(e -> e.getWoodcuttingLevel() <=  Skills.getRealLevel(Skill.WOODCUTTING)).collect(Collectors.toList());
        return axesCanUse.stream().sorted(Comparator.comparing(WoodcuttingAxeEnum::getWoodcuttingLevel)).findFirst().orElse(null).name;
    }
    private int getAttackLevelRequired(){
        return attack_level_required;
    }
    public int getWoodcuttingLevel() {
        return woodcutting_level_required;
    }
    public static boolean canEquip(String name){
        WoodcuttingAxeEnum axe =  Arrays.stream(values()).filter(e -> e.name == name).findFirst().orElse(null);
        return axe.getAttackLevelRequired() < Skills.getRealLevel(Skill.WOODCUTTING);
    }
}
