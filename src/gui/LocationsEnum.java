package gui;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;

import java.util.Arrays;

public enum LocationsEnum {

    BARBARIAN_VILLAGE_FISHING_SPOT("Barbarian Village", new Area()),
    LUMBRIDGE_SWAMP("Lumbridge Swamp", new Area(3245,3160,3238,3148,0)),
    LUMBRIDGE_RIVER("Lumbridge River", new Area()),
    LUMBRIDGE_TELEPORT("Lumbridge Teleport", new Area(3215,3213,3226,3321,0)),
    LOCAL("Surrounding", Players.localPlayer().getTile().getArea(20));

    private String name;
    private Area location;

    private LocationsEnum(String name, Area location){
        this.name = name;
        this.location = location;
    }


    public Area getLocation(){
        return location;
    }
    public String getName(){
        return name;
    }
    public static LocationsEnum getByName(String name){
        return Arrays.stream(values()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }
}
