package tasks.teleportation;

import logic.TeleportationType;
import org.dreambot.api.methods.map.Area;

public abstract class EquipmentTeleport extends TeleportationMethod {
    public EquipmentTeleport(String name, Area destination) {
        super(name, destination, TeleportationType.EQUIPMENT);
    }
}
