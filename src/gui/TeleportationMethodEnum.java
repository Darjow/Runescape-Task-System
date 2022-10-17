package gui;

import tasks.teleportation.TeleportationMethod;
import tasks.teleportation.implementations.LumbridgeTeleport;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TeleportationMethodEnum {

    NONE("None", null),
    LUMBRIDGE_TELEPORT("Lumbridge Home Teleport" ,new LumbridgeTeleport());



    private String name;
    private TeleportationMethod method;

    private TeleportationMethodEnum(String name, TeleportationMethod method){
        this.name = name;
        this.method = method;
    }

    public TeleportationMethod getTeleportationMethod(){
        return method;
    }


    public static List<String> getAllNames(){
        return Arrays.stream(values()).map(e -> e.name).collect(Collectors.toList());
    }
    public static TeleportationMethod getTeleportationMethodByName(String name){
        TeleportationMethodEnum m =  Arrays.stream(values()).filter(e -> e.name.equals(name)).findFirst().orElse(null);
        if(m == null){
            return null;
        }else{
            return m.getTeleportationMethod();
        }
    }
}
