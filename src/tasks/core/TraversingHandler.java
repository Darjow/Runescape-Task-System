package tasks.core;

import main.MainScript;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import tasks.ExecutableTask;

public class TraversingHandler {

    private boolean isTraversing;
    private Area destination;
    private ExecutableTask task;

    public TraversingHandler(){

    }


    public boolean isTraversing(){
        return isTraversing;
    }
    public void setTask(ExecutableTask task){
        this.task = task;
    }
    public boolean isFinished(){
        return destination.contains(Players.localPlayer()) && destination.getRandomTile().getZ() == Players.localPlayer().getTile().getZ();
    }

    private void traverse(){
        MainScript.logInfo(isTraversing);
        isTraversing = true;
        Walking.walk(destination.getRandomTile());

        if(isFinished()){
            isTraversing = false;
        }
        MainScript.logInfo(isTraversing);


    }
    public void traverseToTaskDestination(){
        MainScript.status = "TraversingHandler: Traversing to taskDestination";
        destination = task.getSelectedLocation().getLocation();
        goToLocation();

    }
    public void traverseToBank(){
        MainScript.status = "TraversingHandler: Traversing to bank";
        destination = Bank.getClosestBankLocation().getArea(10);
        traverse();

    }

    public void traverseToGe() {
        MainScript.status = "TraversingHandler: Traversing to GE";
        destination = BankLocation.GRAND_EXCHANGE.getArea(15);
        traverse();
    }


    private void goToLocation() {
        MainScript.status = "TraversingHandler - traversing to task start point.";
        if(task.getTeleportationMethod() == null || !task.getTeleportationMethod().canExecute()){
            Walking.walk(task.getSelectedLocation().getLocation().getRandomTile());
        }else{
            task.getTeleportationMethod().teleport();
            Walking.walk(task.getSelectedLocation().getLocation().getRandomTile());
        }
    }

    public boolean isNearBank(){
        return isTileOnScreen(BankLocation.getNearest(Players.localPlayer()).getTile());
    }
    public boolean isAtGe(){
        return BankLocation.GRAND_EXCHANGE.getCenter().getArea(15).contains(Players.localPlayer());
    }
    public boolean isTileOnScreen(Tile t){
        return org.dreambot.api.methods.map.Map.isTileOnScreen(t) && org.dreambot.api.methods.map.Map.isVisible(t);
    }


}
