package Frames;

import DBConnection.DBConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class TaskChartFrame extends JFrame {

    public TaskChartFrame() {
        // Set up the frame
        setTitle("Task Chart");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create dataset
        DefaultCategoryDataset dataset = createDataset();

        // Create chart
        JFreeChart chart = createChart(dataset);

        // Add chart to the panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);

        setVisible(true); // Show the frame
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String query = "SELECT due_date, COUNT(*) AS task_count FROM TASKS GROUP BY due_date ORDER BY due_date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            while (resultSet.next()) {
                java.sql.Date dueDate = resultSet.getDate("due_date");
                int taskCount = resultSet.getInt("task_count");
                String formattedDate = sdf.format(dueDate);

                dataset.addValue(taskCount, "Task", formattedDate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Task Chart",                   // Chart title
                "Days",                         // X-axis label
                "Number of Tasks",              // Y-axis label
                dataset,                        // Data
                PlotOrientation.VERTICAL,       // Chart orientation
                true,                           // Show legend
                true,                           // Show tooltips
                false                           // No URLs
        );

        // Set up the chart
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        rangeAxis.setAutoRangeIncludesZero(true); // Start from zero
        rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance()); // Format as integers
        rangeAxis.setTickUnit(new NumberTickUnit(1)); // Set tick unit to 1

        return chart;
    }
}
