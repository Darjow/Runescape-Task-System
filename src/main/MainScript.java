package main;



import gui.Gui;

import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.widgets.message.Message;
import tasks.ExecutableTask;
import tasks.TaskManager;
import tasks.teleportation.implementations.LumbridgeTeleport;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@ScriptManifest(description = "Account Builder", name = "Task Framework", category = Category.MISC, author = "Shock#5170", version = 1.0)
public class MainScript extends AbstractScript  {

    public static boolean isRunning = false;
    public static String status = "initializing";


    private TaskManager manager;
    private ExecutableTask currentTask;

    private final long startTime = System.currentTimeMillis();

    @Override
    public void onStart() {
        while(Client.getGameState() != GameState.LOGGED_IN || Client.getGameState() == GameState.LOGIN_SCREEN){
            sleep(1000);
        }
        manager = new TaskManager();
        SwingUtilities.invokeLater(() -> new Gui(manager));
        while(!isRunning){
            sleep(1000);
        }
        currentTask = manager.getTask();
        SkillTracker.resetAll();
        SkillTracker.start(currentTask.getSkill());


    }


    @Override
    public void onPaint(Graphics g){
        if(isRunning && currentTask != null) {
            g.setFont(new Font("Sathu", Font.BOLD, 12));
            g.drawString("Time Running: " + formatTime(), 40, 70);
            g.drawString("Task Executing: " + currentTask.getSkill() + ": " + currentTask.getMethod(), 40, 90);
            g.drawString("Progress: " + Skills.getRealLevel(currentTask.getSkill()) + "/" +  currentTask.getMaxLevel(), 40, 110);
            g.drawString("Total Experience this task: " + SkillTracker.getGainedExperience(currentTask.getSkill()), 40,130);
            g.drawString("Status: " + status, 40, 160);
        }
    }

    public String formatTime(){
        long millis = System.currentTimeMillis() - startTime;
        int sec  = (int)(millis/ 1000) % 60 ;
        int min  = (int)((millis/ (1000*60)) % 60);
        int hr   = (int)((millis/ (1000*60*60)) % 24);
        return String.format("%s:%s:%s", hr, min, sec);
    }


    @Override
    public int onLoop() {
        if(isRunning){
            if(!currentTask.canStart()) {
                manager.initNewTask();
            }else{
                if(currentTask.canExecute()) {
                    currentTask.execute();
                }else{
                    manager.finishedTask();
                    currentTask = manager.getTask();
                    SkillTracker.start(currentTask.getSkill());
                }
            }

        }
        return Calculations.random(300,500);
    }
}
