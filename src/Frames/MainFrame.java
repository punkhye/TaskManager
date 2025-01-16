package Frames;

import DBConnection.DBConnection;
import Data.LoggedUser;
import PopUpWindows.TaskInfoPopUpWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    // LoggedUser and DBConnection initialization
    static LoggedUser loggedUser = new LoggedUser();
    static Connection conn = null;
    static PreparedStatement statement = null;
    static ResultSet result = null;

    // Panels and components
    JPanel loginPlusButtonPanel = new JPanel();
    JPanel loginPanel = new JPanel();
    JPanel updateAndDeletePanel = new JPanel();
    JPanel tablePanel = new JPanel();
    JPanel buttonsOnTheBottomPanel = new JPanel();

    JLabel helloLabel = new JLabel("Hi, " + loggedUser.getUsername());
    JButton logoutButton = new JButton("Logout");
    JButton updateButton = new JButton("Update");
    JButton DeleteButton = new JButton("Delete");
    static JTable table = new JTable();
    static JScrollPane scroll = new JScrollPane(table);
    JButton addTaskButton = new JButton("Add Task");
    JButton sortTaskByPriorityButton = new JButton("Sort by Priority");
    JButton showTaskChartButton = new JButton("Show Task Chart");
    JButton filterByDueDateButton = new JButton("Filter Tasks by Due Date");



    private boolean isAscending = true;



    public MainFrame() {
        setupFrame();
        setupLoginPanel();
        setupTablePanel();
        setupBottomPanel();
        addEventListeners();
    }

    private void setupFrame() {
        this.setSize(700, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(3, 1));  // Променено, за да няма празно място
        this.add(loginPlusButtonPanel);
        this.add(tablePanel);
        this.add(buttonsOnTheBottomPanel);
        this.setVisible(true);
    }

    private void setupLoginPanel() {
        loginPlusButtonPanel.setLayout(new GridLayout(1, 2));
        loginPlusButtonPanel.add(loginPanel);
        loginPlusButtonPanel.add(updateAndDeletePanel);

        loginPanel.setLayout(new FlowLayout());
        loginPanel.add(helloLabel);
        loginPanel.add(logoutButton);

        updateAndDeletePanel.setLayout(new FlowLayout());
        updateAndDeletePanel.add(updateButton);
        updateAndDeletePanel.add(DeleteButton);
    }

    private void setupTablePanel() {
        buildTable(this);

        JLabel taskInfoLabel = new JLabel("Double-click on a task to see it in full detail");

        // Използваме BorderLayout вместо FlowLayout
        tablePanel.setLayout(new BorderLayout());

        // Добавяме label в NORTH
        tablePanel.add(taskInfoLabel, BorderLayout.NORTH);

        // Скролбарът се добавя към центъра на панела
        scroll.setPreferredSize(new Dimension(600, 400));
        tablePanel.add(scroll, BorderLayout.CENTER);

        // Премахваме възможността за редактиране на таблицата
        table.setDefaultEditor(Object.class, null);
    }


    private void setupBottomPanel() {
        buttonsOnTheBottomPanel.add(addTaskButton);
        buttonsOnTheBottomPanel.add(sortTaskByPriorityButton);
        buttonsOnTheBottomPanel.add(showTaskChartButton);
        buttonsOnTheBottomPanel.add(filterByDueDateButton);

        showTaskChartButton.addActionListener(e -> new TaskChartFrame());
        filterByDueDateButton.addActionListener(e -> new FilterTasksByDueDateFrame(MainFrame.this));
        addTaskButton.addActionListener(e -> new AddTaskFrame(MainFrame.this));
    }

    private void addEventListeners() {

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Проверка дали събитието е двойно кликване с левия бутон
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int taskId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                        String taskTitle = table.getValueAt(selectedRow, 1).toString();
                        String taskDescription = table.getValueAt(selectedRow, 2).toString();
                        String taskDueDateString = table.getValueAt(selectedRow, 3).toString();
                        String taskPriority = table.getValueAt(selectedRow, 4).toString();

                        // Check and parse the due date
                        String trimmedDueDate = taskDueDateString.trim();  // Trim whitespace
                        LocalDate targetDate;
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            targetDate = LocalDate.parse(trimmedDueDate, formatter);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(MainFrame.this, "Invalid due date format: " + trimmedDueDate);
                            return;
                        }

                        // If the date is valid, format and pass to the pop-up
                        if (targetDate != null) {
                            new TaskInfoPopUpWindow(taskId, taskTitle, taskDescription, targetDate, taskPriority);
                        }
                    }
                }
            }
        });

        logoutButton.addActionListener(e -> {
            loggedUser.setUser_id(-1);
            loggedUser.setUsername(null);
            loggedUser.setPassword(null);
            this.dispose();
            new LoginFrame();
        });

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                String currentTitle = table.getValueAt(selectedRow, 1).toString();
                String currentDescription = table.getValueAt(selectedRow, 2).toString();
                String currentDueDate = table.getValueAt(selectedRow, 3).toString();
                String currentPriority = table.getValueAt(selectedRow, 4).toString();

                new UpdateTaskFrame(taskId, currentTitle, currentDescription, currentDueDate, currentPriority, MainFrame.this);
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "Please select a task to update.");
            }
        });

        DeleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int taskId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                DeleteTaskHandler.deleteTask(taskId, MainFrame.this);
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "Please select a task to delete.");
            }
        });

        sortTaskByPriorityButton.addActionListener(e -> sortTableByPriority());
    }

    public void updateTable() {
        buildTable(this);
    }

    public static void buildTable(MainFrame mainFrame) {
        conn = DBConnection.getConnection();

        String str = "SELECT task_id, title, description, due_date, priority FROM TASKS";

        try {
            statement = conn.prepareStatement(str);
            result = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Task ID", "Title", "Description", "Due Date", "Priority"});

            while (result.next()) {
                model.addRow(new Object[]{
                        result.getInt("task_id"),
                        result.getString("title"),
                        result.getString("description"),
                        result.getDate("due_date"),
                        result.getString("priority")
                });
            }

            table.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sortTableByPriority() {
        System.out.println("Sorting by priority...");
        String order = isAscending ? "ASC" : "DESC";  // Избор на посока на сортиране

        conn = DBConnection.getConnection();
        String str = "SELECT task_id, title, description, due_date, priority FROM TASKS ORDER BY priority " + order;

        try {
            statement = conn.prepareStatement(str);
            result = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Task ID", "Title", "Description", "Due Date", "Priority"});

            while (result.next()) {
                model.addRow(new Object[]{
                        result.getInt("task_id"),
                        result.getString("title"),
                        result.getString("description"),
                        result.getDate("due_date"),
                        result.getString("priority")
                });
            }

            table.setModel(model);
            System.out.println("Table sorted by priority: " + (isAscending ? "Ascending" : "Descending"));

            isAscending = !isAscending;  // Смяна на посоката за следващото натискане

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
