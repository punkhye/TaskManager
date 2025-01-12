package Frames;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DBConnection.DBConnection;

public class LoginFrame extends JFrame {

    JLabel usernameLabel = new JLabel("Username: ");
    JLabel passwordLabel = new JLabel("Password: ");
    JTextField usernameTextField  = new JTextField(10);
    JPasswordField passwordField = new JPasswordField(10);
    JButton loginButton = new JButton("Login");
    JButton registerButton = new JButton("Register");

    static Connection conn = null;
    static PreparedStatement preparedStatement = null;
    static ResultSet result = null;

    public LoginFrame() {
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Login");
        this.setLayout(new FlowLayout());
        this.add(usernameLabel);
        this.add(usernameTextField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(loginButton);
        this.add(registerButton);

            loginButton.addActionListener(e -> {
                conn = DBConnection.getConnection();
                String username = usernameTextField.getText();
                String password = passwordField.getText();
                String query = "SELECT * FROM USERS WHERE username = ? AND password = ?";
                try {
                    preparedStatement =conn.prepareStatement(query);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    preparedStatement.execute();

                    if (result != null && result.next()) {
                        JOptionPane.showMessageDialog(null, "Login successful!");
                    } else {

                        JOptionPane.showMessageDialog(null, "Invalid username or password.");
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }


            });




        this.setVisible(true);


    }




}
