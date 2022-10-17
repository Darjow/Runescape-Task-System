package tasks;



import gui.LocationsEnum;
import gui.SkillsEnum;
import main.MainScript;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.ScriptManager;
import tasks.core.CoreHandler;
import tasks.implementation.FishingTask;
import tasks.implementation.WoodcuttingTask;
import tasks.teleportation.TeleportationMethod;


import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {

    CoreHandler coreHandler = new CoreHandler(this);



    private List<ExecutableTask> allTasks = new ArrayList<>();
    private List<ExecutableTask> taskList = new LinkedList<>();



    public TaskManager(){
        setAllTasks();
    }


    public List<ExecutableTask> getTasksBySkill(String skill){
        return allTasks.stream().filter(e -> e.getSkill().equals(SkillsEnum.getSkill(skill).getSkill())).collect(Collectors.toList());

    }
    public ExecutableTask getTaskBySkillMethod(String skill, String name){
        return allTasks.stream().filter(e -> e.getSkill().equals(SkillsEnum.getSkill(skill).getSkill()) && e.getMethod().equals(name)).findFirst().orElse(null);
    }
    public void setAllTasks(){
        allTasks.add(new FishingTask("Small net",new String[]{"Net", "Bait"},1,  new HashMap<String, Integer>(){{put("Small fishing net", 1);}}, new ArrayList<>(Arrays.asList(LocationsEnum.LUMBRIDGE_SWAMP))));
        allTasks.add(new FishingTask("Fly fishing",new String[]{"Lure", "Bait"}, 20,new HashMap<String, Integer>(){{put("Feathers", Integer.MAX_VALUE); put("Fly fishing rod", 1);}},new ArrayList<>(Arrays.asList(LocationsEnum.LUMBRIDGE_RIVER, LocationsEnum.BARBARIAN_VILLAGE_FISHING_SPOT))));
        allTasks.add(new WoodcuttingTask("Tree", "Logs",1,new HashMap<String, Integer>(){},new ArrayList<>(Arrays.asList(LocationsEnum.LOCAL))));
        allTasks.add(new WoodcuttingTask("Oak Tree", "Oak logs",15, new HashMap<String, Integer>(){},new ArrayList<>(Arrays.asList(LocationsEnum.LOCAL))));

    }


    public void addTask(String skill, String name, LocationsEnum location, int maxLevel, TeleportationMethod teleportationMethod) {
        ExecutableTask task = getTaskBySkillMethod(skill,name);
        task.setSelectedLocation(location);
        task.setMaxLevel(maxLevel);
        task.setTransportationMethod(teleportationMethod);
        taskList.add(task);
    }


    public void deleteLastAddedTask() {
            taskList.remove(taskList.size()-1);
    }
    public List<ExecutableTask> getAllTasks(){
        return taskList;
        }
    public ExecutableTask getTask(){
        ExecutableTask task =  taskList.get(0);
        if(task == null){
            MainScript.logInfo("Out of tasks, terminating.");
            ScriptManager.getScriptManager().stop();
        }
        return task;
    }
    public void finishedTask(){
        if(taskList.isEmpty()){
            MainScript.logInfo("No more tasks to execute, will log out.");
            Tabs.logout();
            ScriptManager.getScriptManager().stop();
        }else {
            taskList.remove(0);
        }
    }

    public void initNewTask(){
        coreHandler.initTask();
    }

    public List<JComponent> getGuiComponents(String skill, String method) {
        ExecutableTask task =  getTaskBySkillMethod(skill, method);
        if(task == null){
            return null;
        }
        return task.getGuiComponents();
    }

}
