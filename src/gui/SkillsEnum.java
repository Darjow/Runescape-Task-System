package gui;

import org.dreambot.api.methods.skills.Skill;

import java.util.Arrays;

public enum SkillsEnum {

    FISHING("Fishing", Skill.FISHING),
    WOODCUTTING("Woodcutting", Skill.WOODCUTTING);


    private String name;
    private Skill skill;

     SkillsEnum(String name, Skill skill){
        this.name = name;
        this.skill = skill;
    }

    public String getName(){ return name;}
    public Skill getSkill(){return skill;}

    public static SkillsEnum getSkill(String name){
         return Arrays.stream(values()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

}
