package tasks.core;

import jdk.jfr.internal.tool.Main;
import main.MainScript;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.grandexchange.Status;
import org.dreambot.api.script.ScriptManager;

import java.util.HashMap;
import java.util.Map;

public class GrandExchangeHandler {

    private BankingHandler bankingHandler;


    public GrandExchangeHandler(BankingHandler bankingHandler){
        this.bankingHandler = bankingHandler;

    }

        public void execute() {
            if(calculateTotalCost() > Inventory.get(995).getAmount()){
                MainScript.logError("Not enough gold to buy items.");
                ScriptManager.getScriptManager().stop();
            }
    
            if(!GrandExchange.isOpen()){
                openGe();
            }else{
                buyItem(bankingHandler.getItemToBuy().getKey(), bankingHandler.getItemToBuy().getValue());
            }
        }


    private void buyItem(String item, int quantity){
        int price = (int) (LivePrices.get(item) * Calculations.random(1.2, 1.25));

        GrandExchangeItem geItem = null;
        int slot = GrandExchange.getFirstOpenSlot();
        boolean success = false;


        while(!success) {
            MainScript.logInfo("Trying to buy item: " + item);

            if (slot == -1) {
                if (GrandExchange.cancelAll()) {
                    MainScript.sleepUntil(() -> GrandExchange.isReadyToCollect(), 500);
                    GrandExchange.collect();
                } else {
                    if (GrandExchange.isReadyToCollect()) {
                        GrandExchange.collect();
                    }
                }
            } else {
                if (GrandExchange.getItems()[slot].getStatus() == Status.BUY) {
                    geItem = GrandExchange.getItems()[slot];
                    if (GrandExchange.cancelOffer(slot)) {
                        MainScript.logInfo("Succesfully canceled item.");
                    }
                }

                if (geItem != null) {
                    MainScript.logInfo("Adjusted price from " + item);
                    price = (int) (geItem.getPrice() * 1.2);

                }
                if (GrandExchange.buyItem(item, quantity, price)) {
                    MainScript.sleepUntil(() -> GrandExchange.getItems()[slot].getStatus() == Status.BUY_COLLECT, Calculations.random(758, 1500));
                    if (GrandExchange.isReadyToCollect(slot)) {
                        if (GrandExchange.collect()) {
                            success = true;
                            bankingHandler.boughtItem(item);
                        }
                    }
                }

            }
        }


    }
    private void cancelOffer(int slot){
        MainScript.status = "GrandExchangeHandler - Cancelling offer: " + (slot + 1);
        if(GrandExchange.cancelOffer(slot)){
            if(MainScript.sleepUntil(() -> GrandExchange.getItems()[slot].getStatus() == Status.BUY_COLLECT, Calculations.random(500,1500))){
                GrandExchange.collect();
            }
        }
    }
    private void openGe() {
        while(!GrandExchange.isOpen()){
            GrandExchange.open();
            MainScript.sleepUntil(() -> GrandExchange.isOpen(), 1500);
        }
    }

    private int calculateTotalCost() {
        return (int) (bankingHandler.getItemsToBuy().entrySet().stream().mapToInt(e -> LivePrices.get(e.getKey()) * e.getValue()).sum() * 1.25);
    }




}
