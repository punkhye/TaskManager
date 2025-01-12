package PopUpWindows;

import javax.swing.*;
import java.awt.*;

public class TaskInfoPopUpWindow extends JFrame {

    JLabel idLabel = new JLabel("Task Number: ");
    JLabel titleLabel = new JLabel("Title: ");
    JLabel descriptionLabel = new JLabel("Description: ");
    JLabel dueDateLabel = new JLabel("The deadline for completing the task: ");
    JLabel priorityLabel = new JLabel("Priority of this task: ");

    JPanel idPanel = new JPanel();
    JPanel titlePanel = new JPanel();
    JPanel descriptionPanel = new JPanel();
    JPanel dueDatePanel = new JPanel();
    JPanel priorityPanel = new JPanel();
    JPanel buttonPanel = new JPanel();

    JButton okButton= new JButton("Ok");

    public TaskInfoPopUpWindow(int taskId, String title, String description, String dueDate, String priority) {

        this.setSize(500, 650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(6,1));

        this.setTitle("Details of task " + taskId);

        this.add(idPanel);
        this.add(titlePanel);
        this.add(descriptionPanel);
        this.add(dueDatePanel);
        this.add(priorityPanel);
        this.add(buttonPanel);

        idPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        idPanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        descriptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        descriptionPanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        dueDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dueDatePanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
        priorityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        priorityPanel.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));


        idPanel.add(idLabel).setFont(new Font("Calibri", Font.BOLD, 20));
        idPanel.add(new JLabel(String.valueOf(taskId))).setFont(new Font("Arial", Font.PLAIN, 16));

        titlePanel.add(titleLabel).setFont(new Font("Calibri", Font.BOLD, 20));
        titlePanel.add(new JLabel(title)).setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionPanel.add(descriptionLabel).setFont(new Font("Calibri", Font.BOLD, 20));;
        descriptionPanel.add(new JLabel(description)).setFont(new Font("Arial", Font.PLAIN, 16));
        dueDatePanel.add(dueDateLabel).setFont(new Font("Calibri", Font.BOLD, 20));;
        dueDatePanel.add(new JLabel(dueDate)).setFont(new Font("Arial", Font.PLAIN, 16));
        priorityPanel.add(priorityLabel).setFont(new Font("Calibri", Font.BOLD, 20));
        priorityPanel.add(new JLabel(priority)).setFont(new Font("Arial", Font.PLAIN, 16));

        okButton.setPreferredSize(new Dimension(200, 50));
        buttonPanel.add(okButton);

        okButton.addActionListener(e -> {
           this.dispose();
        });

        this.setVisible(true);
    }


}
