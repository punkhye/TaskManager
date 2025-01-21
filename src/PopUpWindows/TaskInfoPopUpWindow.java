package PopUpWindows;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TaskInfoPopUpWindow extends JFrame {

    JLabel idLabel = new JLabel("Task Number: ");
    JLabel titleLabel = new JLabel("Title: ");
    JLabel descriptionLabel = new JLabel("Description: ");
    JLabel dueDateLabel = new JLabel("Deadline (yyyy-MM-dd): ");
    JLabel priorityLabel = new JLabel("Priority of this task: ");

    JPanel idPanel = new JPanel();
    JPanel titlePanel = new JPanel();
    JPanel descriptionPanel = new JPanel();
    JPanel dueDatePanel = new JPanel();
    JPanel priorityPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");

    public TaskInfoPopUpWindow(int taskId, String taskTitle, String taskDescription, LocalDate taskDueDate, String taskPriority) {
        this.setTitle("Task Information");
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridLayout(6, 1));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDueDate = taskDueDate.format(formatter);

        // calculating number of days left
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), taskDueDate);

        // Set up the panels and labels
        idPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        idPanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        idPanel.add(idLabel).setFont(new Font("Calibri", Font.BOLD, 20));
        idPanel.add(new JLabel(String.valueOf(taskId))).setFont(new Font("Arial", Font.PLAIN, 16));

        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        titlePanel.add(titleLabel).setFont(new Font("Calibri", Font.BOLD, 20));
        titlePanel.add(new JLabel(taskTitle)).setFont(new Font("Arial", Font.PLAIN, 14));

        descriptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        descriptionPanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        descriptionPanel.add(descriptionLabel).setFont(new Font("Calibri", Font.BOLD, 20));
        descriptionPanel.add(new JLabel(taskDescription)).setFont(new Font("Arial", Font.PLAIN, 16));

        dueDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dueDatePanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        dueDatePanel.add(dueDateLabel).setFont(new Font("Calibri", Font.BOLD, 20));
        dueDatePanel.add(new JLabel(formattedDueDate)).setFont(new Font("Arial", Font.PLAIN, 16));

        // add days left or the overdue message
        dueDatePanel.add(new JLabel("                 "));
        if (daysLeft > 0) {
            dueDatePanel.add(new JLabel("Days left: ")).setFont(new Font("Calibri", Font.BOLD, 20));
            dueDatePanel.add(new JLabel(String.valueOf(daysLeft))).setFont(new Font("Arial", Font.PLAIN, 16));
        } else {
            dueDatePanel.add(new JLabel("Task Overdue")).setFont(new Font("Calibri", Font.BOLD, 20));
        }

        priorityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        priorityPanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        priorityPanel.add(priorityLabel).setFont(new Font("Calibri", Font.BOLD, 20));
        priorityPanel.add(new JLabel(taskPriority)).setFont(new Font("Arial", Font.PLAIN, 16));

        // ok button
        okButton.setPreferredSize(new Dimension(200, 50));
        buttonPanel.add(okButton);

        okButton.addActionListener(e -> this.dispose());

        // adding the panels
        this.add(idPanel);
        this.add(titlePanel);
        this.add(descriptionPanel);
        this.add(dueDatePanel);
        this.add(priorityPanel);
        this.add(buttonPanel);

        this.setVisible(true);
    }
}
