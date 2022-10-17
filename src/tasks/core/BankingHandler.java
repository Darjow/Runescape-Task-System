package tasks.core;

import main.MainScript;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.ScriptManager;
import tasks.ExecutableTask;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BankingHandler {

    private boolean hasScannedBank = false;
    private boolean hasBanked = false;


    private ExecutableTask task;

    private LinkedHashMap<String, Integer> itemsToBuy = new LinkedHashMap<>();


    public BankingHandler() {


    }

    public void setTask(ExecutableTask task) {
        this.task = task;
    }

    public boolean hasScannedBank() {
        return hasScannedBank;
    }
    public void openBank() {
        MainScript.status = "BankingHandler - opening bank";

        while (!Bank.isOpen()) {
            if (Bank.openClosest()) {
                MainScript.sleepUntil(() -> Bank.isOpen(), 2500);
            }
        }
    }
    public void scanBank() {

        depositItems();

        task.getRequiredItems().entrySet().stream().forEach(e -> {
            if (Bank.get(e.getKey()).isValid() && Bank.get(e.getKey()).getAmount() >= e.getValue()) {
                MainScript.logInfo("We have enough items: " + e.getKey());
            } else {
                if (!Bank.get(e.getKey()).isValid()) {
                    MainScript.logInfo("We dont have any " + e.getKey());
                    itemsToBuy.put(e.getKey(), e.getValue());
                } else {
                    int in_bank = Bank.get(e.getKey()).getAmount();
                    MainScript.logInfo("We need to buy " + (e.getValue() - in_bank));
                    itemsToBuy.put(e.getKey(), e.getValue() - in_bank);
                }
            }
        });

        int amount_spells = 0;

        switch (task.getTeleportationMethod().getTeleportationType()) {
            case TELETAB: {
                amount_spells = getBankQuantity(task.getTeleportationMethod().getName());
                if (amount_spells < 20) {
                    itemsToBuy.put(task.getTeleportationMethod().getName(), 20);
                }
                break;
            }

            case EQUIPMENT: {
                amount_spells = howManyCurrentTeleportsAreThereEquipment();
                if (amount_spells < 30) {
                    itemsToBuy.put(task.getTeleportationMethod().getName(), 10);
                }
                break;
            }
            case SPELLBOOK: {
                amount_spells = howManyCurrentTeleportsAreThereSpell();
                if (amount_spells < 50) {
                    task.getTeleportationMethod().getRequiredItems().entrySet().stream().forEach(e -> {
                        itemsToBuy.put(e.getKey(), e.getValue() * 20);
                    });
                    break;
                }

            }
            default:
                break;
        }
        hasScannedBank = true;
    }
    public boolean needsItems() {
        return !itemsToBuy.isEmpty();
    }
    public void withdrawGold() {
        while(Bank.get(995).isValid() && Bank.get(995).getAmount() != 0){
            Bank.withdrawAll(995);
            MainScript.sleepUntil(() -> Inventory.contains(995), 1000);
        }
    }
    private int howManyCurrentTeleportsAreThereEquipment() {
        if (!Bank.isOpen()) {
            openBank();
        }
        String name = task.getTeleportationMethod().getName().split("\\(")[0]; //ring of duelling (8)
        int amount = 0;
        for (int i = 1; i < 8; i++) {
            if (Bank.get(String.format("%s(%d)", name, i)).isValid()) {
                amount += (Bank.get(String.format("%s(%d)", name, i)).getAmount() * i);
            }
        }
        return amount;
    }
    private int howManyCurrentTeleportsAreThereSpell() {
        HashMap<String, Integer> requiredItems = task.getTeleportationMethod().getRequiredItems();
        HashMap<String, Integer> hasItems = new HashMap<>();
        HashMap<String, Integer> hasTeleports = new HashMap<>();

        if(requiredItems == null) return 100;
        requiredItems.entrySet().stream().forEach(e -> {
            if (Bank.get(e.getKey()).isValid()) {
                hasItems.put(e.getKey(), Bank.get(e.getKey()).getAmount());
            } else {
                hasItems.put(e.getKey(), 0);
            }
        });


        for (Map.Entry<String, Integer> set : hasItems.entrySet()) {
            hasTeleports.put(set.getKey(), Math.round(set.getValue() / requiredItems.get(set.getKey())));
        }

        return hasTeleports.entrySet().stream().map(e -> e.getValue()).min(Integer::compare).orElse(null);


    }
    private int getBankQuantity(String item) {
        if (!Bank.isOpen()) {
            openBank();
        }
        return Bank.get(item).isValid() ? Bank.get(item).getAmount() : 0;


    }
    private void depositItems() {
        if(Bank.isOpen()){
            while(!Inventory.isEmpty()){
                Bank.depositAllItems();
                MainScript.sleepUntil(() -> Inventory.isEmpty(), 500);
            }
        }
    }
    public boolean hasBanked() {
        return hasBanked;
    }
    public void grabAllRequiredItems() {
        MainScript.status = "BankingHandler - grabAllRequiredItems";

        if(Inventory.size() > 0){
            Bank.depositAllItems();
        }
        for(Map.Entry<String, Integer> keyset : task.getRequiredItems().entrySet()){
            if(Bank.get(keyset.getKey()) != null && Bank.get(keyset.getKey()).getAmount() >= keyset.getValue()){
                while(Inventory.get(keyset.getKey()) == null || Inventory.get(keyset.getKey()).getAmount() != keyset.getValue()){
                    int inventoryUsed = Inventory.size();
                    MainScript.logInfo("Withdrawing: " + keyset.getKey() + ", amount: " + keyset.getValue());
                    if(Bank.withdraw(keyset.getKey(), keyset.getValue())){
                        MainScript.sleepUntil(() -> Inventory.contains(keyset.getKey()) && Inventory.size() == inventoryUsed + keyset.getValue(), 1500);
                    }
                    if(Inventory.size() != inventoryUsed + keyset.getValue()){
                        MainScript.logInfo("-- InventoryUsed: " + inventoryUsed + " != " + (Inventory.all().size() - keyset.getValue()));
                        MainScript.logInfo("Error withdrawing item.");
                        Bank.depositAll(keyset.getKey());
                    }
                }
            }else{
                MainScript.logError("We dont have the required items in bank");
                ScriptManager.getScriptManager().stop();
            }
        }

        if (task.getTeleportationMethod().getRequiredItems() != null) {
            MainScript.status = "BankingHandler - grabbing teleport";
            task.getTeleportationMethod().getRequiredItems().entrySet().stream().forEach(e -> {
                if(Bank.get(e.getKey()).isValid() && Bank.get(e.getKey()).getAmount() >= e.getValue()){
                    if(Bank.withdraw(e.getKey(), e.getValue())){
                        MainScript.sleepUntil(() -> Inventory.contains(e.getKey()), 500);
                    }
                }else{
                    MainScript.logError("Core Handler - grabTeleport - No teleport items in bank");
                }
            });
        }

    hasBanked = true;

}
    public LinkedHashMap<String, Integer> getItemsToBuy() {
        return itemsToBuy;
    }
    public Map.Entry<String, Integer> getItemToBuy(){
        return itemsToBuy.size() == 0? null : itemsToBuy.entrySet().iterator().next();
    }

    public void boughtItem(String item) {
        itemsToBuy.remove(item);
    }

    public void closeBank() {
        MainScript.status = "BankingHandler - closing bank";

        while (Bank.isOpen()) {
            if (Bank.close()) {
                MainScript.sleepUntil(() -> !Bank.isOpen(), 250);
            }
        }
    }
}


