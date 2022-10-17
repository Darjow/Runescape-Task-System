package tasks.teleportation;

import logic.TeleportationType;
import org.dreambot.api.methods.map.Area;

import java.util.HashMap;

public abstract class MinigameTeleport extends TeleportationMethod{

    protected long timer;

    public MinigameTeleport(String name, Area destination) {
        super(name, destination, TeleportationType.MINIGAME);
        this.timer = 0;
    }






}
