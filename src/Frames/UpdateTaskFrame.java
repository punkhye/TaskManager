package Frames;

import DBConnection.DBConnection;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class UpdateTaskFrame extends JFrame {

    private final int taskId;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JDatePickerImpl datePicker;  // Date picker for due date
    private JComboBox<String> priorityComboBox;
    private final MainFrame mainFrame;

    public UpdateTaskFrame(int taskId, String currentTitle, String currentDescription, String currentDueDate, String currentPriority, MainFrame mainFrame) {
        this.taskId = taskId;
        this.mainFrame = mainFrame;

        // Frame settings
        setTitle("Update Task");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        // Title field
        JPanel titlePanel = createTitlePanel(currentTitle);
        // Description field
        JPanel descriptionPanel = createDescriptionPanel(currentDescription);
        // Due date field (using date picker)
        JPanel dueDatePanel = createDueDatePanel(currentDueDate);
        // Priority field
        JPanel priorityPanel = createPriorityPanel(currentPriority);
        // Buttons
        JPanel buttonsPanel = createButtonsPanel();

        // Add panels to the frame
        add(titlePanel);
        add(descriptionPanel);
        add(dueDatePanel);
        add(priorityPanel);
        add(buttonsPanel);

        setVisible(true);
    }

    private JPanel createTitlePanel(String currentTitle) {
        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.add(new JLabel("Title:"));
        titleField = new JTextField(20);
        titleField.setText(currentTitle);
        titlePanel.add(titleField);
        return titlePanel;
    }

    private JPanel createDescriptionPanel(String currentDescription) {
        JPanel descriptionPanel = new JPanel(new FlowLayout());
        descriptionPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setText(currentDescription);
        descriptionPanel.add(new JScrollPane(descriptionArea));
        return descriptionPanel;
    }

    private JPanel createDueDatePanel(String currentDueDate) {
        JPanel dueDatePanel = new JPanel(new FlowLayout());
        dueDatePanel.add(new JLabel("Due Date:"));

        // Create date picker
        datePicker = createDatePicker(currentDueDate);

        dueDatePanel.add(datePicker);

        return dueDatePanel;
    }

    private JPanel createPriorityPanel(String currentPriority) {
        JPanel priorityPanel = new JPanel(new FlowLayout());
        priorityPanel.add(new JLabel("Priority:"));

        String[] priorityOptions = {"Low Priority", "Medium Priority", "High Priority"};
        priorityComboBox = new JComboBox<>(priorityOptions);
        priorityComboBox.setSelectedItem(currentPriority);

        priorityPanel.add(priorityComboBox);
        return priorityPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        updateButton.addActionListener(e -> updateTask());
        cancelButton.addActionListener(e -> dispose());

        buttonsPanel.add(updateButton);
        buttonsPanel.add(cancelButton);
        return buttonsPanel;
    }

    private JDatePickerImpl createDatePicker(String currentDueDate) {
        // Date model and properties
        UtilDateModel model = new UtilDateModel();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Parse the currentDueDate into the model's date format
        try {
            java.util.Date parsedDate = dateFormat.parse(currentDueDate);
            model.setDate(parsedDate.getYear() + 1900, parsedDate.getMonth(), parsedDate.getDate());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format.");
            e.printStackTrace();
        }

        // Date picker setup
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private void updateTask() {
        String newTitle = titleField.getText();
        String newDescription = descriptionArea.getText();
        Object selectedValue = datePicker.getModel().getValue();

        // Validate date
        if (!(selectedValue instanceof java.util.Date utilDate)) {
            JOptionPane.showMessageDialog(this, "Invalid date format.");
            return;
        }

        java.sql.Date newDueDate = new java.sql.Date(utilDate.getTime());
        String newPriority = (String) priorityComboBox.getSelectedItem();

        if (newTitle.isEmpty() || newDescription.isEmpty() || newPriority == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        // Update task in the database
        try (Connection conn = DBConnection.getConnection()) {
            String updateSQL = "UPDATE tasks SET title = ?, description = ?, due_date = ?, priority = ? WHERE task_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                stmt.setString(1, newTitle);
                stmt.setString(2, newDescription);
                stmt.setDate(3, newDueDate);  // Saving the date in the correct format
                stmt.setString(4, newPriority);
                stmt.setInt(5, taskId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Task updated successfully!");
                mainFrame.updateTable();  // Update table in the main window
                dispose();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating task.");
        }
    }

    // Formatter for date picker
    static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String datePattern = "yyyy-MM-dd";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        public String valueToString(Object value) {
            if (value != null) {
                if (value instanceof java.util.Date) {
                    return dateFormatter.format((java.util.Date) value);
                } else if (value instanceof java.util.Calendar) {
                    return dateFormatter.format(((java.util.Calendar) value).getTime());
                }
            }
            return "";
        }
    }
}
