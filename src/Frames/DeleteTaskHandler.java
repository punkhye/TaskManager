package Frames;

import DBConnection.DBConnection;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteTaskHandler {

    public static void deleteTask(int taskId, JFrame parentFrame) {
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
                parentFrame,
                "Are you sure you want to delete this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Try deleting the task from the database
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(
                         "DELETE FROM TASKS WHERE task_id = ?")) {

                statement.setInt(1, taskId);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(parentFrame, "Task deleted successfully!");

                    // Update the table in MainFrame after deletion
                    if (parentFrame instanceof MainFrame mainFrame) {
                        mainFrame.updateTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Error deleting task.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Database error: " + e.getMessage());
            }
        }
    }
}
