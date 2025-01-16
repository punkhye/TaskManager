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
import java.util.Calendar;
import java.util.Properties;

public class AddTaskFrame extends JDialog {
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JDatePickerImpl datePicker;
    private final JComboBox<String> priorityComboBox;
    private final MainFrame mainFrame;

    public AddTaskFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Dialog setup
        setTitle("Add New Task");
        setSize(400, 400);
        setLayout(new GridLayout(6, 2));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Title field
        add(new JLabel("Title:"));
        titleField = new JTextField();
        add(titleField);

        // Description field
        add(new JLabel("Description:"));
        descriptionArea = new JTextArea();
        add(new JScrollPane(descriptionArea));

        // Due date picker
        add(new JLabel("Due Date:"));
        datePicker = createDatePicker();
        add(datePicker);

        // Priority combo box
        add(new JLabel("Priority:"));
        String[] priorities = {"Low Priority", "Medium Priority", "High Priority"};
        priorityComboBox = new JComboBox<>(priorities);
        add(priorityComboBox);

        // Buttons
        JButton addButton = new JButton("Save");
        add(addButton);
        addButton.addActionListener(event -> addTaskToDatabase());

        JButton cancelButton = new JButton("Cancel");
        add(cancelButton);
        cancelButton.addActionListener(event -> dispose());

        // Display settings
        setModal(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JDatePickerImpl createDatePicker() {
        // Date model and properties
        UtilDateModel model = new UtilDateModel();
        model.setDate(2025, 1, 12);

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        // Date picker setup
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private void addTaskToDatabase() {
        // Retrieve input values
        String title = titleField.getText();
        String description = descriptionArea.getText();
        Object selectedValue = datePicker.getModel().getValue();

        // Validate date
        if (!(selectedValue instanceof java.util.Date utilDate)) {
            JOptionPane.showMessageDialog(this, "Invalid date format.");
            return;
        }

        java.sql.Date dueDate = new java.sql.Date(utilDate.getTime());
        String priority = (String) priorityComboBox.getSelectedItem();

        if (title.isEmpty() || description.isEmpty() || priority == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        // Insert task into database
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO tasks (title, description, due_date, priority) VALUES (?, ?, ?, ?)")) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setDate(3, dueDate);
            stmt.setString(4, priority);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Task added successfully!");

            // Update table in MainFrame and close dialog
            mainFrame.updateTable();
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding task: " + ex.getMessage());
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

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
