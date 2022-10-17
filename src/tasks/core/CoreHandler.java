package tasks.core;


import main.MainScript;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import tasks.ExecutableTask;
import tasks.TaskManager;


public class CoreHandler {

    private TaskManager manager;
    private TraversingHandler traversingHandler;
    private GrandExchangeHandler geHandler;
    private BankingHandler bankingHandler;

    private ExecutableTask task;


    public CoreHandler(TaskManager manager) {
        this.manager = manager;
        this.traversingHandler = new TraversingHandler();
        this.bankingHandler = new BankingHandler();
        this.geHandler = new GrandExchangeHandler(bankingHandler);
    }

    public void initTask() {
        this.task = manager.getTask();
        this.traversingHandler.setTask(task);
        this.bankingHandler.setTask(task);


        if (!bankingHandler.hasScannedBank()) {
            if (!traversingHandler.isNearBank() && !traversingHandler.isTraversing()) {
                traversingHandler.traverseToBank();
            } else {
                if (!Bank.isOpen()) {
                    bankingHandler.openBank();
                } else {
                    bankingHandler.scanBank();
                }
            }
        } else {
            if (!bankingHandler.needsItems()) {
                if (!bankingHandler.hasBanked()) {
                    bankingHandler.grabAllRequiredItems();
                }

                if (task.isBankSkill()) {
                    task.canStart(true);
                } else {
                    if(!traversingHandler.isTraversing()){
                        if(Bank.isOpen()) bankingHandler.closeBank();
                        traversingHandler.traverseToTaskDestination();
                        if(traversingHandler.isFinished()) {
                            task.canStart(true);
                        }
                    }
                }
            } else {
                if (!traversingHandler.isAtGe()) {
                    if (!Inventory.contains(995)) {
                        bankingHandler.withdrawGold();
                    } else {
                        if(!traversingHandler.isTraversing()) {
                            traversingHandler.traverseToGe();
                        }
                    }
                } else {
                    geHandler.execute();
                }

            }
        }
    }
}


