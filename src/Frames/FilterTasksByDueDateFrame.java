package Frames;

import DBConnection.DBConnection;
import PopUpWindows.TaskInfoPopUpWindow;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Properties;

public class FilterTasksByDueDateFrame extends JFrame {

    private JDatePickerImpl datePicker;
    private JButton filterButton;
    private JTable resultTable;
    private JScrollPane scrollPane;

    public FilterTasksByDueDateFrame(MainFrame mainFrame) {
        setTitle("Filter Tasks by Due Date");
        setSize(600, 400);
        setLayout(new BorderLayout(10, 10));

        // Initialize date picker
        initializeDatePicker();

        // Initialize filter button
        initializeFilterButton();

        // Initialize result table
        initializeResultTable();



        // Create and add panels to the dialog
        createAndAddPanels();

        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void initializeDatePicker() {
        UtilDateModel model = new UtilDateModel();
        model.setDate(2025, 1, 12);  // Set initial date
        Properties properties = new Properties();
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private void initializeFilterButton() {
        filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> filterTasksByDueDate());
    }

    private void initializeResultTable() {
        resultTable = new JTable();
        resultTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
        resultTable.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(resultTable);
        //event listener for the task info window
        addEventListeners();

    }

    private void createAndAddPanels() {
        // Top panel with date picker
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Select Due Date:");
        topPanel.add(label);
        topPanel.add(datePicker);

        // Center panel with table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with filter button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(filterButton);

        // Add panels to the main dialog window
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        resultTable.setDefaultEditor(Object.class, null);
    }

    private void filterTasksByDueDate() {
        java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
        if (selectedDate != null) {
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT task_id, title, description, due_date, priority FROM TASKS WHERE due_date = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setDate(1, sqlDate);

                ResultSet result = stmt.executeQuery();

                // Update the table with results
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

                resultTable.setModel(model);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a valid date.");
        }
    }

    private void addEventListeners() {

        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Проверка дали събитието е двойно кликване с левия бутон
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int selectedRow = resultTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int taskId = Integer.parseInt(resultTable.getValueAt(selectedRow, 0).toString());
                        String taskTitle = resultTable.getValueAt(selectedRow, 1).toString();
                        String taskDescription = resultTable.getValueAt(selectedRow, 2).toString();
                        String taskDueDateString = resultTable.getValueAt(selectedRow, 3).toString();
                        String taskPriority = resultTable.getValueAt(selectedRow, 4).toString();

                        // Check and parse the due date
                        String trimmedDueDate = taskDueDateString.trim();  // Trim whitespace
                        LocalDate targetDate;
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            targetDate = LocalDate.parse(trimmedDueDate, formatter);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(FilterTasksByDueDateFrame.this, "Invalid due date format: " + trimmedDueDate);
                            return;
                        }

                        // If the date is valid, format and pass to the pop-up
                        new TaskInfoPopUpWindow(taskId, taskTitle, taskDescription, targetDate, taskPriority);
                    }
                }
            }
        });
    }

    // Inner class to format the date
    static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private final String datePattern = "yyyy-MM-dd";  // Format is "yyyy-MM-dd"
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());  // Format as "yyyy-MM-dd"
            }
            return "";
        }
    }
}
