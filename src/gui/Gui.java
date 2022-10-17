package gui;

import jdk.jfr.internal.tool.Main;
import main.MainScript;
import org.dreambot.api.methods.skills.Skills;
import tasks.ExecutableTask;
import tasks.TaskManager;
import tasks.teleportation.TeleportationMethod;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Gui extends JFrame {

    private TaskManager manager;

    private String skill = null;
    private String method = null;
    private LocationsEnum location = null;
    private int maxLevel = 0;
    private TeleportationMethod teleportationMethod = null;


    JComboBox comboSkill = new JComboBox();
    JComboBox comboMethod = new JComboBox();
    JComboBox comboLocation = new JComboBox();
    JTextField tfMaxLevel = new JTextField();
    JButton btnAdd = new JButton("Add task");
    JComboBox comboTeleportation = new JComboBox();

    JLabel lblError = new JLabel();
    JTextArea taTasks = new JTextArea();
    JButton btnStart = new JButton("Start script");
    JButton btnDeleteTask = new JButton("Delete task");


    JPanel left = new JPanel();
    List<JComponent> components = new ArrayList<>();

    JPanel right = new JPanel();


    public Gui(TaskManager manager) {
            this.manager = manager;

            this.setTitle("Selecting Tasks");
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.setPreferredSize(new Dimension(700, 500));
            this.getContentPane().setLayout(new GridLayout());
            this.setMaximumSize(new Dimension(100, 100));

            left.setLayout(new GridLayout(8,2,30,30));
            right.setSize(new Dimension(300, 300));


            left.add(new JLabel("Skill:") );
            left.add(comboSkill);

            Arrays.stream(SkillsEnum.values()).forEach(e -> {
                comboSkill.addItem(e.getName());
            });


            comboSkill.addActionListener(e -> {
                try {
                        skill = (String) comboSkill.getSelectedItem();
                        comboMethod.removeAllItems();
                        List<ExecutableTask> tasks = manager.getTasksBySkill(skill);
                        if (tasks != null) {
                            tasks.forEach(f -> {
                                comboMethod.addItem(f.getMethod());

                            });
                        renderComponents();
                    }
                }catch(Exception kaka){
                    MainScript.logError(kaka.getMessage());
                }

                    });
                comboSkill.setPopupVisible(false);


            left.add(new JLabel("Method:"));
            left.add(comboMethod);
            comboMethod.addActionListener(e -> {
                try {
                    comboLocation.removeAllItems();
                    method = (String) comboMethod.getSelectedItem();
                    ExecutableTask task = manager.getTaskBySkillMethod(skill, method);
                    if (task != null) {
                        task.getLocations().stream().forEach(f -> comboLocation.addItem(f.getName()));
                    }
                    renderComponents();
                }catch(Exception kaka){
                    MainScript.logError(kaka.getMessage());
                }
            });
            comboMethod.setPopupVisible(false);

            left.add(new JLabel("Location"));
            left.add(comboLocation);
            comboLocation.addActionListener(e -> {
                String selectedItem = (String) ((JComboBox) e.getSource()).getSelectedItem();
                location = LocationsEnum.getByName(selectedItem);
                comboLocation.setPopupVisible(false);

            });


            left.add(new JLabel("Teleportation Method"));
            TeleportationMethodEnum.getAllNames().forEach(e -> comboTeleportation.addItem(e));
            left.add(comboTeleportation);
            comboTeleportation.addActionListener(e -> {
                teleportationMethod = TeleportationMethodEnum.getTeleportationMethodByName((String) ((JComboBox) e.getSource()).getSelectedItem());
            });
            left.add(new JLabel("Max Level:"));
            left.add(tfMaxLevel);
            tfMaxLevel.addCaretListener(e -> {
                try {
                    maxLevel = Integer.parseInt(((JTextField) e.getSource()).getText());
                } catch (NumberFormatException f) {

                }
            });

            lblError.setVisible(false);
            lblError.setForeground(new Color(255, 0, 0));
            left.add(lblError);
            left.add(btnAdd);
            btnAdd.addActionListener(e -> {
                if (skill != null && method != null && maxLevel != 0 && location != null && teleportationMethod != null) {
                    if (Skills.getRealLevel(SkillsEnum.getSkill(skill).getSkill()) < manager.getTaskBySkillMethod(skill, method).getMinLevel()) {
                        MainScript.logInfo("Failed to add a new task.");
                        MainScript.logInfo(skill + " level is not high enough.");
                        lblError.setText(skill + " level is not high enough.");
                        lblError.setVisible(true);
                    } else {
                        MainScript.logInfo("Added a new task.");
                        manager.addTask(skill, method, location, maxLevel, teleportationMethod);
                        taTasks.append(String.format("%n%s", manager.getTaskBySkillMethod(skill, method)));
                        lblError.setVisible(false);

                    }
                } else {
                    MainScript.logInfo("Failed to add a new task.");
                    lblError.setText("Must provide valid options");
                    lblError.setVisible(true);

                }
            });


            taTasks.setMargin(new Insets(10, 10, 10, 10));
            taTasks.setSize(new Dimension(300, 200));
            btnDeleteTask.setSize(new Dimension(75, 40));
            btnStart.setSize(new Dimension(75, 40));
            taTasks.setEditable(false);


            btnDeleteTask.addActionListener(e -> {
                manager.deleteLastAddedTask();
                taTasks.setText(manager.getAllTasks().stream().map(f -> f.toString()).collect(Collectors.joining("\n")));
            });

            btnStart.addActionListener(e -> {
                if (manager.getAllTasks().isEmpty()) {
                    lblError.setText("Please provide a task");
                    lblError.setVisible(true);
                } else {
                    MainScript.isRunning = true;
                    this.setVisible(false);
                    this.dispose();
                }
            });
            right.add(btnDeleteTask);
            right.add(btnStart);
            right.add(taTasks);


            this.pack();
            this.setVisible(true);

            this.getContentPane().add(left);
            this.getContentPane().add(right);


    }

    private void renderComponents() {
        if (components != null && components.size() > 0) {
            components.forEach(e -> left.remove(e));
            components = null;
        }
        components = manager.getGuiComponents(skill, method);
        if (components != null) {
            left.remove(btnAdd); left.remove(lblError);
            components.forEach(e -> left.add(e));
            left.add(lblError); left.add(btnAdd);
        }
    }
    }

