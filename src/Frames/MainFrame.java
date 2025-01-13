package Frames;

import DBConnection.DBConnection;
import Data.LoggedUser;
import Data.TModel;
import PopUpWindows.TaskInfoPopUpWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MainFrame extends JFrame {

      static LoggedUser loggedUser = new LoggedUser();
        JPanel loginPlusButtonPanel = new JPanel();
        JPanel loginPanel = new JPanel();
        JPanel updateAndDeletePanel = new JPanel();
        JLabel helloLabel = new JLabel("Hi, " + loggedUser.getUsername());
        JButton logoutButton = new JButton("Logout");
        JButton updateButton = new JButton("Update");
        JButton DeleteButton = new JButton("Delete");


        JPanel tablePanel = new JPanel();
       static JTable table = new JTable();
       static JScrollPane scroll = new JScrollPane(table);

        JPanel buttonsOnTheBottomPanel = new JPanel();
        JButton addTaskButton = new JButton("Add Task");
        JButton sortTaskByPriorityButton = new JButton("Sort by Priority");
        JButton showTaskForACertainDayButton = new JButton("Show Task For A Certain Day");

    static Connection conn = null;
    static PreparedStatement statement = null;
    static ResultSet result = null;

    public MainFrame() {

        this.setSize(550, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(3,1));
        this.add(loginPlusButtonPanel);
        this.add(tablePanel);
        this.add(buttonsOnTheBottomPanel);

        loginPlusButtonPanel.setLayout(new GridLayout(1,2));
        loginPlusButtonPanel.add(loginPanel);
        loginPlusButtonPanel.add(updateAndDeletePanel);

        loginPanel.setLayout(new FlowLayout());
        loginPanel.add(helloLabel);
        loginPanel.add(logoutButton);

        updateAndDeletePanel.setLayout(new FlowLayout());
        updateAndDeletePanel.add(updateButton);
        updateAndDeletePanel.add(DeleteButton);

        buildTable();
        tablePanel.add(new JLabel("Double-click on a task to see it in full detail"));
        tablePanel.add(scroll);

        buttonsOnTheBottomPanel.add(addTaskButton);
        buttonsOnTheBottomPanel.add(sortTaskByPriorityButton);
        buttonsOnTheBottomPanel.add(showTaskForACertainDayButton);


        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {

                    int taskId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                    String taskTitle = table.getValueAt(selectedRow, 1).toString();
                    String taskDescription = table.getValueAt(selectedRow, 2).toString();
                    String taskDueDateString = table.getValueAt(selectedRow, 3).toString();
                    String taskPriority = table.getValueAt(selectedRow, 4).toString();

                    // Convert the string to a Date object
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    java.util.Date parsedDate = null;
                    try {
                        parsedDate = sdf.parse(taskDueDateString);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    // If the date is valid, format and pass to the pop-up
                    if (parsedDate != null) {
                        Date taskDueDate = new Date(parsedDate.getTime()); // Converts to java.sql.Date
                        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(taskDueDate); // Format the date as dd-MM-yyyy

                        if (e.getClickCount() == 2) {
                            new TaskInfoPopUpWindow(taskId, taskTitle, taskDescription, formattedDate, taskPriority);
                        }
                    }
                }
            }
        });

        logoutButton.addActionListener(e ->{

            loggedUser.setUser_id(-1);
            loggedUser.setUsername(null);
            loggedUser.setPassword(null);
            this.dispose();
            LoginFrame loginFrame = new LoginFrame();

        });


        this.setVisible(true);

    }


    public static void buildTable() {
        conn=DBConnection.getConnection();
        String str;
        str="SELECT * from TASKS";
        try {
            statement=conn.prepareStatement(str);
            result=statement.executeQuery();

            String[] colsNames = {"Number","Title","Description", "Due Date", "Priority"};
            table.setModel(new TModel(result, colsNames));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
