package tasks.teleportation;

import logic.TeleportationType;
import org.dreambot.api.methods.map.Area;

public abstract class TeletabTeleport extends TeleportationMethod {

    public TeletabTeleport(String name, Area destination) {
        super(name, destination, TeleportationType.TELETAB);
    }

}
